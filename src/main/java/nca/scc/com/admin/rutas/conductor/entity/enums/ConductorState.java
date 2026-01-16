package nca.scc.com.admin.rutas.conductor.entity.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ConductorState {
    disponible,
    asignado,
    inactivo;

    @JsonCreator
    public static ConductorState from(String v) {
        if (v == null) return null;
        return ConductorState.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
