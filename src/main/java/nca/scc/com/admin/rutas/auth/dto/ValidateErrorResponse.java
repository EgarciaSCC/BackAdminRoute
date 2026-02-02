package nca.scc.com.admin.rutas.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta de error de validate (401). Contrato: valid=false, message.
 */
public class ValidateErrorResponse {

    private final boolean valid = false;
    private final String message;

    public ValidateErrorResponse(String message) {
        this.message = message;
    }

    @JsonProperty("valid")
    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
}
