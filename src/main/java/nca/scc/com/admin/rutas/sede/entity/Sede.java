package nca.scc.com.admin.rutas.sede.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "sede")
public class Sede {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    private String nombre;

    private String direccion;
    private String ciudad;
    private double lat;
    private double lng;

    public Sede() {}

    @JsonCreator
    public Sede(
            @JsonProperty("id") String id,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("direccion") String direccion,
            @JsonProperty("ciudad") String ciudad,
            @JsonProperty("lat") double lat,
            @JsonProperty("lng") double lng) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.lat = lat;
        this.lng = lng;
    }

    @PrePersist
    public void ensureId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String direccion() { return direccion; }
    public void direccion(String direccion) { this.direccion = direccion; }
    public String ciudad() { return ciudad; }
    public void ciudad(String ciudad) { this.ciudad = ciudad; }
    public double lat() { return lat; }
    public void lat(double lat) { this.lat = lat; }
    public double lng() { return lng; }
    public void lng(double lng) { this.lng = lng; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sede)) return false;
        return Objects.equals(id, ((Sede) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
