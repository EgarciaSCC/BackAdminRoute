package nca.scc.com.admin.rutas.bus.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MotorType {
    combustible,
    hibrido,
    electrico,
    otro;

    @JsonCreator
    public static MotorType from(String v) {
        if (v == null) return null;
        return MotorType.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}

