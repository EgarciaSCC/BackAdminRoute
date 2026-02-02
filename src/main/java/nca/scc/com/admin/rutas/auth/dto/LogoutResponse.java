package nca.scc.com.admin.rutas.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta exitosa de logout (200). Contrato: success, message.
 */
public class LogoutResponse {

    private final boolean success = true;
    private final String message;

    public LogoutResponse(String message) {
        this.message = message;
    }

    @JsonProperty("success")
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
