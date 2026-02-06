package nca.scc.com.admin.rutas.ruta.entity.enums;

public enum TipoRuta {
    RECOGIDA("Recogida en domicilio"),
    LLEVADA("Llevada a domicilio");

    private final String descripcion;

    TipoRuta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
