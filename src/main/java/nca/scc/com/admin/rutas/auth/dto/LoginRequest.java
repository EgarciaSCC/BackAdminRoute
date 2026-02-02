package nca.scc.com.admin.rutas.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request de login. Frontend puede enviar credenciales en claro o cifradas (AES-256).
 * Validación: requeridos; si en claro: username max 50, password min 8.
 * Si vienen cifrados, el string puede ser largo (validamos solo not blank).
 */
public class LoginRequest {

    @NotBlank(message = "username es requerido")
    @Size(max = 500)
    private String username;

    @NotBlank(message = "password es requerido")
    @Size(max = 500)
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
