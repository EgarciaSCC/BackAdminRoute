package nca.scc.com.admin.rutas.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta de error de login (401). Contrato: success=false, message.
 */
public class LoginErrorResponse {

    private final boolean success = false;
    private final String message;

    public LoginErrorResponse(String message) {
        this.message = message;
    }

    @JsonProperty("success")
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
