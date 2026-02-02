package nca.scc.com.admin.rutas.auth;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.auth.dto.*;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final AuthService authService;

    public TokenController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        if (authService.isBlocked(clientIp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginErrorResponse("Demasiados intentos fallidos. Intente más tarde."));
        }

        String username = authService.resolveCredential(req.getUsername());
        String password = authService.resolveCredential(req.getPassword());

        if (username == null || username.isBlank() || password == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginErrorResponse("Usuario o contraseña incorrectos"));
        }
        // Contrato: password mínimo 8 caracteres cuando se envía en claro (sin AES)
        if (!authService.isAesEnabled() && password.length() < 8) {
            authService.registerFailedAttempt(clientIp);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginErrorResponse("Usuario o contraseña incorrectos"));
        }

        var maybeUser = authService.findByUsername(username);
        if (maybeUser.isEmpty()) {
            authService.registerFailedAttempt(clientIp);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginErrorResponse("Usuario o contraseña incorrectos"));
        }

        Usuario user = maybeUser.get();
        if (!authService.checkPassword(password, user.getPassword())) {
            authService.registerFailedAttempt(clientIp);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginErrorResponse("Usuario o contraseña incorrectos"));
        }

        authService.clearFailedAttemptsOnSuccess(clientIp);

        try {
            long expiresSeconds = 3600L;
            String token = authService.generateTokenForUser(user, expiresSeconds);
            UserDto userDto = AuthService.toUserDto(user);
            return ResponseEntity.ok(new LoginResponse("Login exitoso", token, userDto));
        } catch (JOSEException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginErrorResponse("Error al generar el token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7).trim();
            if (!token.isEmpty()) {
                authService.addTokenToBlacklist(token);
            }
        }
        return ResponseEntity.ok(new LogoutResponse("Sesión cerrada exitosamente"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidateErrorResponse("Token inválido o expirado"));
        }

        var maybeUser = authService.validateToken(authorization);
        if (maybeUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidateErrorResponse("Token inválido o expirado"));
        }

        UserDto userDto = AuthService.toUserDto(maybeUser.get());
        return ResponseEntity.ok(new ValidateResponse(userDto));
    }

    private static String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
    }
}
