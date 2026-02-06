package nca.scc.com.admin.rutas.colegio.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "colegio", indexes = {
        @Index(name = "idx_colegio_nit", columnList = "nit"),
        @Index(name = "idx_colegio_tenant", columnList = "tenant")
})
public class Colegio {
    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 30)
    private String nit;

    @NotBlank
    private String nombre;

    private String direccion;
    private String ciudad;
    private String contacto;

    @Email
    private String email;

    @NotBlank
    private String tenant;

    @Column(name = "logo_url")
    private String logoUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull
    @Column(nullable = false)
    private Boolean activo = true;

    public Colegio() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
    }

    @JsonCreator
    public Colegio(
            @JsonProperty("id") String id,
            @JsonProperty("nit") String nit,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("direccion") String direccion,
            @JsonProperty("ciudad") String ciudad,
            @JsonProperty("contacto") String contacto,
            @JsonProperty("email") String email,
            @JsonProperty("tenant") String tenant,
            @JsonProperty("logoUrl") String logoUrl) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.nit = nit;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.contacto = contacto;
        this.email = email;
        this.tenant = tenant;
        this.logoUrl = logoUrl;
        this.activo = true;
    }

    @PrePersist
    public void ensureId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void updateTimestamp() {
        updatedAt = LocalDateTime.now();
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Colegio)) return false;
        return Objects.equals(id, ((Colegio) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}