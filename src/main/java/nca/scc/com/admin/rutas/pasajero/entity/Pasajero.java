package nca.scc.com.admin.rutas.pasajero.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import nca.scc.com.admin.rutas.sede.entity.Sede;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pasajero")
public class Pasajero {

    @Id
    @Column(length = 36)
    private String id;

    private String nombre;
    private String curso;
    private String direccion;
    private String barrio;
    private double lat;
    private double lng;
    private boolean asignado;

    public Pasajero() {}

    @JsonCreator
    public Pasajero(
            @JsonProperty("id") String id,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("curso") String curso,
            @JsonProperty("direccion") String direccion,
            @JsonProperty("barrio") String barrio,
            @JsonProperty("lat") double lat,
            @JsonProperty("lng") double lng,
            @JsonProperty("asignado") boolean asignado) {
        this.id = id;
        this.nombre = nombre;
        this.curso = curso;
        this.direccion = direccion;
        this.barrio = barrio;
        this.lat = lat;
        this.lng = lng;
        this.asignado = asignado;
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
    public String curso() { return curso; }
    public void curso(String curso) { this.curso = curso; }
    public String direccion() { return direccion; }
    public void direccion(String direccion) { this.direccion = direccion; }
    public String barrio() { return barrio; }
    public void barrio(String barrio) { this.barrio = direccion; }
    public double lat() { return lat; }
    public void lat(double lat) { this.lat = lat; }
    public double lng() { return lng; }
    public void lng(double lng) { this.lng = lng; }
    public boolean asignado() { return asignado; }
    public void asignado(boolean asignado) { this.asignado = asignado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pasajero)) return false;
        return Objects.equals(id, ((Pasajero) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
