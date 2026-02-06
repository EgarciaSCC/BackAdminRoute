package nca.scc.com.admin.rutas.pasajero.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad Pasajero/Estudiante - Alumno inscrito en una ruta
 *
 * Aislamiento por tenant automático
 * Relación con Sede y Padre
 * Coordenadas geográficas para ubicación de domicilio
 */
@Entity
@Table(name = "pasajero", indexes = {
    @Index(name = "idx_pasajero_sede", columnList = "sede_id"),
    @Index(name = "idx_pasajero_padre", columnList = "padre_id"),
    @Index(name = "idx_pasajero_tenant", columnList = "tenant")
})
public class Pasajero {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank(message = "El nombre del estudiante es requerido")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La matrícula es requerida")
    @Column(unique = true, nullable = false)
    private String matricula;

    @NotBlank(message = "El curso es requerido")
    @Column(nullable = false)
    private String curso;

    @NotBlank(message = "La dirección es requerida")
    @Column(nullable = false)
    private String direccion;

    private String barrio;

    private Double lat;
    private Double lng;

    @NotNull(message = "La sede es requerida")
    @Column(name = "sede_id", nullable = false, length = 36)
    private String sedeId;

    @Column(name = "padre_id", length = 36)
    private String padreId;

    private String telefonoEmergencia;
    private String alergias;
    private String notas;

    @Column(nullable = false)
    private String tenant;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean activo = true;

    public Pasajero() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
    }

    @JsonCreator
    public Pasajero(
            @JsonProperty("id") String id,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("matricula") String matricula,
            @JsonProperty("curso") String curso,
            @JsonProperty("direccion") String direccion,
            @JsonProperty("barrio") String barrio,
            @JsonProperty("lat") Double lat,
            @JsonProperty("lng") Double lng,
            @JsonProperty("sedeId") String sedeId,
            @JsonProperty("padreId") String padreId,
            @JsonProperty("tenant") String tenant) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.nombre = nombre;
        this.matricula = matricula;
        this.curso = curso;
        this.direccion = direccion;
        this.barrio = barrio;
        this.lat = lat;
        this.lng = lng;
        this.sedeId = sedeId;
        this.padreId = padreId;
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
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public String getSedeId() { return sedeId; }
    public void setSedeId(String sedeId) { this.sedeId = sedeId; }
    public String getPadreId() { return padreId; }
    public void setPadreId(String padreId) { this.padreId = padreId; }
    public String getTelefonoEmergencia() { return telefonoEmergencia; }
    public void setTelefonoEmergencia(String telefonoEmergencia) { this.telefonoEmergencia = telefonoEmergencia; }
    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
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
        if (!(o instanceof Pasajero)) return false;
        return Objects.equals(id, ((Pasajero) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pasajero{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", matricula='" + matricula + '\'' +
                ", curso='" + curso + '\'' +
                ", activo=" + activo +
                '}';
    }
}
