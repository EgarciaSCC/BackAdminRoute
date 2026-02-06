package nca.scc.com.admin.rutas.coordinador.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nca.scc.com.admin.rutas.coordinador.entity.enums.CoordinadorState;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad Coordinador - Supervisor de rutas
 *
 * Aislamiento por tenant automático
 * Campos sensibles: cedula, telefono, email
 */
@Entity
@Table(name = "coordinador", indexes = {
    @Index(name = "idx_coordinador_cedula", columnList = "cedula"),
    @Index(name = "idx_coordinador_tenant", columnList = "tenant")
})
public class Coordinador {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank(message = "El nombre del coordinador es requerido")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La cédula es requerida")
    @Column(unique = true, nullable = false)
    private String cedula;

    @NotBlank(message = "El teléfono es requerido")
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    @Column(nullable = false)
    private String email;

    @NotNull(message = "El estado es requerido")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoordinadorState estado;

    @Column(nullable = false)
    private String tenant;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean activo = true;

    public Coordinador() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
    }

    @JsonCreator
    public Coordinador(
            @JsonProperty("id") String id,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("cedula") String cedula,
            @JsonProperty("telefono") String telefono,
            @JsonProperty("email") String email,
            @JsonProperty("estado") CoordinadorState estado,
            @JsonProperty("tenant") String tenant) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado != null ? estado : CoordinadorState.activo;
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
            this.estado = CoordinadorState.activo;
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
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public CoordinadorState getEstado() { return estado; }
    public void setEstado(CoordinadorState estado) { this.estado = estado; }
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
        if (!(o instanceof Coordinador)) return false;
        return Objects.equals(id, ((Coordinador) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Coordinador{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                ", activo=" + activo +
                '}';
    }
}
