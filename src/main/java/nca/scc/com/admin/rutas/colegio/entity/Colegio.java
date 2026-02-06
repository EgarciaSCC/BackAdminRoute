package nca.scc.com.admin.rutas.conductor.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nca.scc.com.admin.rutas.conductor.entity.enums.ConductorState;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad Conductor - Operador de rutas escolares
 *
 * Campos sensibles (encriptables): cedula, telefono, licencia
 * Aislamiento por tenant automático
 * Estados: disponible, asignado, inactivo
 */
@Entity
@Table(name = "conductor", indexes = {
        @Index(name = "idx_conductor_cedula", columnList = "cedula"),
        @Index(name = "idx_conductor_licencia", columnList = "licencia"),
        @Index(name = "idx_conductor_tenant", columnList = "tenant")
})
public class Conductor {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank(message = "El nombre del conductor es requerido")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La cédula es requerida")
    @Column(unique = true, nullable = false)
    private String cedula;

    @NotBlank(message = "El teléfono es requerido")
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "El número de licencia es requerido")
    @Column(unique = true, nullable = false)
    private String licencia;

    private String tipoLicencia; // A, B, C, etc

    @NotNull(message = "El estado es requerido")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConductorState estado;

    @Column(nullable = false)
    private String tenant;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean activo = true;

    public Conductor() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
        this.estado = ConductorState.disponible;
    }

    @JsonCreator
    public Conductor(
            @JsonProperty("id") String id,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("cedula") String cedula,
            @JsonProperty("telefono") String telefono,
            @JsonProperty("licencia") String licencia,
            @JsonProperty("tipoLicencia") String tipoLicencia,
            @JsonProperty("estado") ConductorState estado,
            @JsonProperty("tenant") String tenant) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.licencia = licencia;
        this.tipoLicencia = tipoLicencia;
        this.estado = estado != null ? estado : ConductorState.disponible;
        this.tenant = tenant;
        this.activo = true;
    }

    @PrePersist
    public void ensureIdAndTimestamp() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = ConductorState.disponible;
        }
    }

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // ...existing code...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    public String getTipoLicencia() { return tipoLicencia; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }
    public ConductorState getEstado() { return estado; }
    public void setEstado(ConductorState estado) { this.estado = estado; }
    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conductor)) return false;
        return Objects.equals(id, ((Conductor) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Conductor{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                ", activo=" + activo +
                '}';
    }
}
