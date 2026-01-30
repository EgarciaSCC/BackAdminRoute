package nca.scc.com.admin.rutas.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import nca.scc.com.admin.config.JwtProperties;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtProperties props;

    public AuthService(UsuarioRepository usuarioRepository, JwtProperties props) {
        this.usuarioRepository = usuarioRepository;
        this.props = props;
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public boolean checkPassword(String raw, String hashed) {
        if (hashed == null) return false;
        try {
            return BCrypt.checkpw(raw, hashed);
        } catch (Exception e) {
            return false;
        }
    }

    public String generateTokenForUser(Usuario user, long expiresInSeconds) throws JOSEException {
        String secret = props.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret no está configurado. No es posible generar tokens HS256.");
        }

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expiresInSeconds);

        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp));

        if (user.getTenant() != null && !user.getTenant().isBlank()) {
            claims.claim(props.getClaimTenant(), user.getTenant());
        }

        if (user.getRole() != null) {
            claims.claim("role", user.getRole().name());
        }

        SignedJWT signedJWT = new SignedJWT(new com.nimbusds.jose.JWSHeader(JWSAlgorithm.HS256), claims.build());

        // Preparar la clave para MACSigner: requiere al menos 256 bits (32 bytes).
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
}
