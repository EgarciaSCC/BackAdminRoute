package nca.scc.com.admin.rutas.historial.dto;

import jakarta.validation.constraints.NotBlank;

public class DropoffPasajeroRequest {

    @NotBlank
    private String historialRutaId;

    @NotBlank
    private String rutaId;

    @NotBlank
    private String pasajeroId;

    @NotBlank
    private String fecha;

    // getters & setters

    public String getRutaId() {
        return rutaId;
    }

    public void setRutaId(String rutaId) {
        this.rutaId = rutaId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPasajeroId() {
        return pasajeroId;
    }

    public void setPasajeroId(String pasajeroId) {
        this.pasajeroId = pasajeroId;
    }

    public String getHistorialRutaId() {
        return historialRutaId;
    }

    public void setHistorialRutaId(String historialRutaId) {
        this.historialRutaId = historialRutaId;
    }
}
