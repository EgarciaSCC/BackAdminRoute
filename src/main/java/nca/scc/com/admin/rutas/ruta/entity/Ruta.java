package nca.scc.com.admin.rutas.ruta.entity;

import jakarta.persistence.*;
import nca.scc.com.admin.rutas.ruta.entity.enums.TipoRuta;

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
    @Column(name = "pasajeros")
    private List<String> estudiantes;

    private String estado;
    private String createdAt;

    // Nuevos campos para validación de asignación
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ruta")
    private TipoRuta tipoRuta;

    @Column(name = "hora_inicio")
    private String horaInicio;

    @Column(name = "hora_fin")
    private String horaFin;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "turno")
    private String turno; // 'manana', 'tarde'

    @Column(name = "capacidad_actual")
    private Integer capacidadActual = 0;

    @Column(name = "tenant")
    private String tenant;

    // Constructor sin parámetros para JPA
    public Ruta() {
        this.id = UUID.randomUUID().toString();
        this.capacidadActual = 0;
    }

    // Hook para asegurar que siempre hay un ID antes de persistir
    @PrePersist
    public void ensureId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // getters & setters (standard)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    public String getConductorId() { return conductorId; }
    public String getCoordinadorId() { return coordinadorId; }
    public String getSedeId() { return sedeId; }
    public void setSedeId(String sedeId) { this.sedeId = sedeId; }

    // compatibility methods (legacy style used across codebase)
    public String busId() { return this.busId; }
    public void busId(String busId) { this.busId = busId; }

    public void conductorId(String conductorId) { this.conductorId = conductorId; }

    public void coordinadorId(String coordinadorId) { this.coordinadorId = coordinadorId; }

    public String sedeId() { return this.sedeId; }
    public void sedeId(String sedeId) { this.sedeId = sedeId; }

    // nuevo: getters/setters estándar para estudiantes (usados por servicios)
    public List<String> getEstudiantes() { return estudiantes; }
    public void setEstudiantes(List<String> estudiantes) { this.estudiantes = estudiantes; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // Getters y setters para nuevos campos
    public TipoRuta getTipoRuta() { return tipoRuta; }
    public void setTipoRuta(TipoRuta tipoRuta) { this.tipoRuta = tipoRuta; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }


    public Integer getCapacidadActual() { return capacidadActual; }
    public void setCapacidadActual(Integer capacidadActual) { this.capacidadActual = capacidadActual; }

    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }

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
