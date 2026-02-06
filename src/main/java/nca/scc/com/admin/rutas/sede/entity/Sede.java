package nca.scc.com.admin.rutas.sede.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "sede")
public class Sede {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    @Column(nullable = false)
    private String colegioId;

    @NotBlank
    private String nombre;

    private String direccion;
    private String ciudad;
    private double lat;
    private double lng;

    // Id de la empresa de transporte (tenant) que administra esta sede en el sistema
    private String transportId;

    // Tenant (propietario de la sede)
    @NotBlank
    private String tenant;

    public Sede() {}

    public Sede(String id, String colegioId, String nombre, String direccion,
                String ciudad, double lat, double lng, String tenant) {
        this.id = id;
        this.colegioId = colegioId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.lat = lat;
        this.lng = lng;
        this.tenant = tenant;
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
    public String getColegioId() { return colegioId; }
    public void setColegioId(String colegioId) { this.colegioId = colegioId; }
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
    public String getTransportId() { return transportId; }
    public void setTransportId(String transportId) { this.transportId = transportId; }
    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }

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
