package nca.scc.com.admin.rutas.rutaPasajeros.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class RutaPasajeroId implements Serializable {

    @Column(name = "ruta_id")
    private String rutaId;

    @Column(name = "pasajero_id")
    private String pasajeroId;

    public RutaPasajeroId() {}

    public RutaPasajeroId(String rutaId, String pasajeroId) {
        this.rutaId = rutaId;
        this.pasajeroId = pasajeroId;
    }

    public String getRutaId() {
        return rutaId;
    }

    public String getPasajeroId() {
        return pasajeroId;
    }

    public void setRutaId(String rutaId) {
        this.rutaId = rutaId;
    }

    public void setPasajeroId(String pasajeroId) {
        this.pasajeroId = pasajeroId;
    }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RutaPasajeroId that = (RutaPasajeroId) o;
        return rutaId.equals(that.rutaId) && pasajeroId.equals(that.pasajeroId);
    }

    @Override
    public int hashCode() {
        return rutaId.hashCode() * 31 + pasajeroId.hashCode();
    }
}
