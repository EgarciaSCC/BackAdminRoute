package nca.scc.com.admin.rutas.historial.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoHistorialRuta {
    programada,
    iniciada,
    en_curso,
    completada,
    parcial,
    cancelada;

    @JsonCreator
    public static EstadoHistorialRuta from(String v) {
        return v == null ? null : EstadoHistorialRuta.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}

