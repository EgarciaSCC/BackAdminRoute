package nca.scc.com.admin.rutas.bus.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BusState {
    activo,
    mantenimiento,
    inactivo;

    @JsonCreator
    public static BusState from(String v) {
        if (v == null) return null;
        return BusState.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}

