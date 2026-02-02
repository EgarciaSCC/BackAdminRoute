package nca.scc.com.admin.rutas.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta exitosa de validate (200). Contrato: valid=true, user.
 */
public class ValidateResponse {

    private final boolean valid = true;
    private final UserDto user;

    public ValidateResponse(UserDto user) {
        this.user = user;
    }

    @JsonProperty("valid")
    public boolean isValid() { return valid; }
    public UserDto getUser() { return user; }
}
