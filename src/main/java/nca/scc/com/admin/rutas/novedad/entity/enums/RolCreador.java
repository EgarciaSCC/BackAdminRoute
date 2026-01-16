package nca.scc.com.admin.rutas.novedad.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RolCreador {
    coordinador,
    padre,
    administrador;

    @JsonCreator
    public static RolCreador from(String v) {
        return v == null ? null : RolCreador.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
