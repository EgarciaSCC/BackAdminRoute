package nca.scc.com.admin.rutas.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class RutaPasajerosRequest {

    @NotBlank
    private String rutaId;

    @NotEmpty
    private List<String> pasajeros;

    // getters y setters

    public String getRutaId() {
        return rutaId;
    }

    public void setRutaId(String rutaId) {
        this.rutaId = rutaId;
    }

    public List<String> getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(List<String> pasajeros) {
        this.pasajeros = pasajeros;
    }

}
