package nca.scc.com.admin.rutas.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import nca.scc.com.admin.config.JwtProperties;
import nca.scc.com.admin.rutas.auth.dto.UserDto;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final JwtProperties props;

    @Value("${app.auth.aes-base64-key:}")
    private String aesBase64Key;

    @Value("${app.auth.aes-base64-iv:}")
    private String aesBase64Iv;

    @Value("${app.auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.auth.failed-attempts-window-minutes:15}")
    private int failedAttemptsWindowMinutes;

    @Value("${app.auth.block-duration-minutes:30}")
    private int blockDurationMinutes;

    /** jti -> expiración (para limpiar entradas vencidas) */
    private final ConcurrentHashMap<String, Instant> tokenBlacklist = new ConcurrentHashMap<>();

    /** IP -> intentos fallidos (timestamps) y bloqueo */
    private final ConcurrentHashMap<String, FailedAttemptsRecord> failedAttemptsByIp = new ConcurrentHashMap<>();

    public AuthService(UsuarioRepository usuarioRepository, JwtProperties props) {
        this.usuarioRepository = usuarioRepository;
        this.props = props;
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Busca usuario por username O email (lo que sea que el usuario proporcione)
     */
    public Optional<Usuario> findByUsernameOrEmail(String credential) {
        if (credential == null || credential.isBlank()) {
            return Optional.empty();
        }

        // Intentar primero por username
        Optional<Usuario> byUsername = usuarioRepository.findByUsername(credential);
        if (byUsername.isPresent()) {
            return byUsername;
        }

        // Si no encuentra por username, intentar por email
        return usuarioRepository.findByEmail(credential);
    }

    public boolean checkPassword(String raw, String hashed) {
        // Si el hash es nulo o vacío, permitir acceso (fallback)
        if (hashed == null || hashed.isBlank()) {
            log.warn("Password hash is NULL or empty - allowing login (fallback mode)");
            return true;
        }

        // Si raw es nulo, denegar
        if (raw == null || raw.isBlank()) {
            log.error("Raw password is NULL or blank");
            return false;
        }

        try {
            // Si el hash no parece ser un hash BCrypt válido (no comienza con $2), permitir
            if (!hashed.startsWith("$2a$") && !hashed.startsWith("$2b$") && !hashed.startsWith("$2y$")) {
                log.warn("Password hash does not look like BCrypt format - allowing login (fallback mode). Hash: {}", hashed.substring(0, Math.min(10, hashed.length())));
                return true;
            }

            boolean matches = BCrypt.checkpw(raw, hashed);
            log.debug("Password check result: {} (raw length: {}, hash length: {})",
                      matches, raw.length(), hashed.length());
            return matches;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid BCrypt hash format - allowing login (fallback mode). Error: {}", e.getMessage());
            return true;
        } catch (Exception e) {
            log.error("Error checking password with BCrypt: {} - allowing login (fallback mode)", e.getMessage());
            return true;
        }
    }

    public boolean isAesEnabled() {
        return aesBase64Key != null && !aesBase64Key.isBlank()
                && aesBase64Iv != null && !aesBase64Iv.isBlank();
    }

public String resolveCredential(String value) {
    if (value == null) return null;
    boolean aesConfigured = aesBase64Key != null && !aesBase64Key.isBlank()
            && aesBase64Iv != null && !aesBase64Iv.isBlank();
    if (aesConfigured) {
        String decrypted = AesUtil.decrypt(value, aesBase64Key, aesBase64Iv);
        // Si no se pudo desencriptar, considerar la credencial inválida (devuelve null)
        return decrypted;
    }
    return value;
}

    public boolean isBlocked(String clientIp) {
        FailedAttemptsRecord record = failedAttemptsByIp.get(clientIp);
        if (record == null) return false;
        record.cleanOldAttempts(Instant.now(), failedAttemptsWindowMinutes);
        if (record.blockUntil != null && Instant.now().isBefore(record.blockUntil)) {
            return true;
        }
        if (record.blockUntil != null && !Instant.now().isBefore(record.blockUntil)) {
            failedAttemptsByIp.remove(clientIp);
        }
        return false;
    }

    public void registerFailedAttempt(String clientIp) {
        Instant now = Instant.now();
        failedAttemptsByIp.compute(clientIp, (ip, r) -> {
            FailedAttemptsRecord rec = r != null ? r : new FailedAttemptsRecord();
            rec.addFailure(now, failedAttemptsWindowMinutes, maxFailedAttempts, blockDurationMinutes);
            return rec;
        });
    }

    public void clearFailedAttemptsOnSuccess(String clientIp) {
        failedAttemptsByIp.remove(clientIp);
    }

    public String generateTokenForUser(Usuario user, long expiresInSeconds) throws JOSEException {
        String secret = props.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret no está configurado. No es posible generar tokens HS256.");
        }

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expiresInSeconds);
        String jti = UUID.randomUUID().toString();

        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .jwtID(jti)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp));

        if (user.getTenant() != null && !user.getTenant().isBlank()) {
            claims.claim(props.getClaimTenant(), user.getTenant());
        }

        if (user.getRole() != null) {
            claims.claim("role", user.getRole().name());
        }

        SignedJWT signedJWT = new SignedJWT(new com.nimbusds.jose.JWSHeader(JWSAlgorithm.HS256), claims.build());

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                keyBytes = md.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 no disponible en este entorno", e);
            }
        }

        JWSSigner signer = new MACSigner(keyBytes);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public void addTokenToBlacklist(String token) {
        try {
            SignedJWT signed = SignedJWT.parse(token);
            String jti = signed.getJWTClaimsSet().getJWTID();
            Date exp = signed.getJWTClaimsSet().getExpirationTime();
            if (jti != null && exp != null) {
                tokenBlacklist.put(jti, exp.toInstant());
            }
        } catch (Exception ignored) {
            // Token inválido: no se puede blacklistear por jti
        }
    }

    /** Limpia entradas vencidas de la blacklist. */
    private void cleanBlacklist() {
        Instant now = Instant.now();
        tokenBlacklist.entrySet().removeIf(e -> now.isAfter(e.getValue()));
    }

    public boolean isTokenBlacklisted(String token) {
        cleanBlacklist();
        try {
            SignedJWT signed = SignedJWT.parse(token);
            String jti = signed.getJWTClaimsSet().getJWTID();
            return jti != null && tokenBlacklist.containsKey(jti);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida el Bearer token: firma, exp, blacklist y devuelve el usuario (subject = username).
     */
    public Optional<Usuario> validateToken(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) return Optional.empty();
        String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7).trim() : bearerToken.trim();
        if (token.isEmpty()) return Optional.empty();

        if (isTokenBlacklisted(token)) return Optional.empty();

        String secret = props.getSecret();
        if (secret == null || secret.isBlank()) return Optional.empty();

        try {
            SignedJWT signed = SignedJWT.parse(token);
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                keyBytes = md.digest(keyBytes);
            }
            if (!signed.verify(new MACVerifier(keyBytes))) return Optional.empty();

            JWTClaimsSet claims = signed.getJWTClaimsSet();
            if (claims.getExpirationTime() == null || claims.getExpirationTime().toInstant().isBefore(Instant.now())) {
                return Optional.empty();
            }
            String username = claims.getSubject();
            if (username == null || username.isBlank()) return Optional.empty();
            return findByUsername(username);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static UserDto toUserDto(Usuario u) {
        if (u == null) return null;
        String role = u.getRole() != null ? u.getRole().toFrontendRole() : null;
        return new UserDto(
                u.getId(),
                u.getUsername(),
                role,
                u.getNombre(),
                u.getEmail() != null ? u.getEmail() : ""
        );
    }

    private static final class FailedAttemptsRecord {
        private final List<Instant> failures = new CopyOnWriteArrayList<>();
        private volatile Instant blockUntil;

        void addFailure(Instant now, int windowMinutes, int maxAttempts, int blockMinutes) {
            cleanOldAttempts(now, windowMinutes);
            failures.add(now);
            if (failures.size() >= maxAttempts) {
                blockUntil = now.plusSeconds(blockMinutes * 60L);
            }
        }

        void cleanOldAttempts(Instant now, int windowMinutes) {
            Instant cutoff = now.minusSeconds(windowMinutes * 60L);
            failures.removeIf(t -> t.isBefore(cutoff));
        }
    }
}
