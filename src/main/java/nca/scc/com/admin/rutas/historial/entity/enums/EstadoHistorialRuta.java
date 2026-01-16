package nca.scc.com.admin.rutas.historial.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoHistorialRuta {
    completada,
    cancelada,
    parcial;

    @JsonCreator
    public static EstadoHistorialRuta from(String v) {
        return v == null ? null : EstadoHistorialRuta.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
