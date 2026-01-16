package nca.scc.com.admin.rutas.coordinador.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CoordinadorState {
    disponible,
    asignado,
    inactivo;

    @JsonCreator
    public static CoordinadorState from(String v) {
        return v == null ? null : valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
