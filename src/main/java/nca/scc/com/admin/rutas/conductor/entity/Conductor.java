package nca.scc.com.admin.rutas.conductor.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nca.scc.com.admin.rutas.conductor.entity.enums.ConductorState;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "conductor")
public class Conductor {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String cedula;

    @NotBlank
    private String telefono;

    @NotBlank
    private String licencia;

    // nuevo campo: tipo de licencia (opcional)
    @Column(name = "tipo_licencia")
    private String tipoLicencia;

    @NotNull
    private ConductorState estado;

    @NotBlank
    private String tenant;

    private Boolean activo = true;

    public Conductor() {
        this.id = UUID.randomUUID().toString();
        this.activo = true;
    }

    @JsonCreator
    public Conductor(
            @JsonProperty("id") String id,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("cedula") String cedula,
            @JsonProperty("telefono") String telefono,
            @JsonProperty("licencia") String licencia,
            @JsonProperty("estado") ConductorState estado,
            @JsonProperty("tenant") String tenant,
            @JsonProperty("tipoLicencia") String tipoLicencia) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.licencia = licencia;
        this.estado = estado;
        this.tenant = tenant;
        this.tipoLicencia = tipoLicencia;
    }

    // getters & setters
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
}
