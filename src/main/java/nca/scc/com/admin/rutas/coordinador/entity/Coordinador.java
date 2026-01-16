package nca.scc.com.admin.rutas.coordinador.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nca.scc.com.admin.rutas.coordinador.entity.enums.CoordinadorState;

import java.util.UUID;

@Entity
@Table(name = "coordinador")
public class Coordinador {

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
    private String email;

    @NotNull
    private CoordinadorState estado;

    public Coordinador() {}

    @JsonCreator
    public Coordinador(
            @JsonProperty("id") String id,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("cedula") String cedula,
            @JsonProperty("telefono") String telefono,
            @JsonProperty("email") String email,
            @JsonProperty("estado") CoordinadorState estado) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado;
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
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public CoordinadorState getEstado() { return estado; }
    public void setEstado(CoordinadorState estado) { this.estado = estado; }
}
