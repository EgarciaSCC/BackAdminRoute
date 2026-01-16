package nca.scc.com.admin.rutas.novedad.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NovedadCategoria {
    cancelacion_ruta,
    cancelacion_parada,
    cambio_horario,
    incidente,
    otro;

    @JsonCreator
    public static NovedadCategoria from(String v) {
        return v == null ? null : NovedadCategoria.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
