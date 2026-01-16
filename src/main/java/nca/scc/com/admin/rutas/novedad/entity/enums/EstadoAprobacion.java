package nca.scc.com.admin.rutas.novedad.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoAprobacion {
    pendiente,
    aprobada,
    rechazada;

    @JsonCreator
    public static EstadoAprobacion from(String v) {
        return v == null ? null : EstadoAprobacion.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
