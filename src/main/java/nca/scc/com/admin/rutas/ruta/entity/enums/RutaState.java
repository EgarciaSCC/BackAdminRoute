package nca.scc.com.admin.rutas.ruta.entity.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RutaState {
    activa,
    inactiva;

    @JsonCreator
    public static RutaState from(String v) {
        if (v == null) return null;
        return RutaState.valueOf(v.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}

