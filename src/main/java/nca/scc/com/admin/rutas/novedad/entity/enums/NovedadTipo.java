package nca.scc.com.admin.rutas.novedad.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NovedadTipo {
    info,
    alerta,
    urgente;

    @JsonCreator
    public static NovedadTipo from(String v) {
        return v == null ? null : NovedadTipo.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
