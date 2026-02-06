package nca.scc.com.admin.rutas.coordinador.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Estados posibles de un Coordinador
 */
public enum CoordinadorState {
    activo("Activo"),
    inactivo("Inactivo");

    private final String descripcion;

    CoordinadorState(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @JsonCreator
    public static CoordinadorState from(String v) {
        if (v == null) return null;
        try {
            return CoordinadorState.valueOf(v.toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
