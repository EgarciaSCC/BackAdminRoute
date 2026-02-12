package nca.scc.com.admin.rutas.historial.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoEventoPasajero {
    PICKUP,
    DROPOFF;

    @JsonCreator
    public static EstadoHistorialRuta from(String v) {
        return v == null ? null : EstadoHistorialRuta.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}

