package nca.scc.com.admin.rutas.auth;

import com.nimbusds.jose.JOSEException;
import nca.scc.com.admin.config.JwtProperties;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final JwtProperties props;
    private final AuthService authService;

    public TokenController(JwtProperties props, AuthService authService) {
        this.props = props;
        this.authService = authService;
    }

    record TokenRequest(String sub, String tid, Long expiresInSeconds) {}
    record TokenResponse(String token) {}

    record LoginRequest(String username, String password, Long expiresInSeconds) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) throws JOSEException {
        if (req.username == null || req.username.isBlank() || req.password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "username and password are required"));
        }

        var maybe = authService.findByUsername(req.username);
        if (maybe.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid_credentials"));
        }

        Usuario user = maybe.get();
        if (!authService.checkPassword(req.password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid_credentials"));
        }

        long expires = req.expiresInSeconds == null ? 3600L : req.expiresInSeconds;
        String token = authService.generateTokenForUser(user, expires);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
