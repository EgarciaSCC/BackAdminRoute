package nca.scc.com.admin.rutas.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta exitosa de login (200). Contrato: success, message, token, user.
 */
public class LoginResponse {

    private final boolean success = true;
    private final String message;
    private final String token;
    private final UserDto user;

    public LoginResponse(String message, String token, UserDto user) {
        this.message = message;
        this.token = token;
        this.user = user;
    }

    @JsonProperty("success")
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserDto getUser() { return user; }
}
