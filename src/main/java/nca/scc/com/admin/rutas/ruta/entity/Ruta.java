package nca.scc.com.admin.rutas.ruta.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ruta")
public class Ruta {

    @Id
    @Column(length = 36)
    private String id;

    private String nombre;
    private String busId;
    private String conductorId;
    private String coordinadorId;
    private String sedeId;

    @ElementCollection
    private List<String> estudiantes;

    private String estado;
    private String createdAt;

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
    public String busId() { return busId; }
    public void busId(String busId) { this.busId = busId; }
    public String conductorId() { return conductorId; }
    public void conductorId(String conductorId) { this.conductorId = conductorId; }
    public String coordinadorId() { return conductorId; }
    public void coordinadorId(String coordinadorId) { this.coordinadorId = coordinadorId; }
    public String sedeId() { return sedeId; }
    public void sedeId(String sedeId) { this.sedeId = sedeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ruta)) return false;
        return Objects.equals(id, ((Ruta) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
