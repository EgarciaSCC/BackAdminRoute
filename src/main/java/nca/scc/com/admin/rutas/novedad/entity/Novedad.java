package nca.scc.com.admin.rutas.novedad.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nca.scc.com.admin.rutas.novedad.entity.enums.EstadoAprobacion;
import nca.scc.com.admin.rutas.novedad.entity.enums.NovedadCategoria;
import nca.scc.com.admin.rutas.novedad.entity.enums.NovedadTipo;
import nca.scc.com.admin.rutas.novedad.entity.enums.RolCreador;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Novedad")
public class Novedad {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    @Column(nullable = false, length = 36)
    private String rutaId;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NovedadTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NovedadCategoria categoria;

    @NotNull
    @Column(nullable = false)
    private Boolean requiereAprobacion;

    @Enumerated(EnumType.STRING)
    private EstadoAprobacion estadoAprobacion;

    @NotBlank
    @Column(nullable = false)
    private String creadoPor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolCreador rolCreador;

    @Column(length = 36)
    private String estudianteId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean leida;

    private String aprobadoPor;

    private LocalDateTime fechaAprobacion;

    @Column(length = 500)
    private String comentarioAprobacion;

    /* ======================
       Constructors
       ====================== */

    public Novedad() {
    }

    @PrePersist
    public void ensureId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

/* ======================
       Getters & Setters
       ====================== */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRutaId() {
        return rutaId;
    }

    public void setRutaId(String rutaId) {
        this.rutaId = rutaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public NovedadTipo getTipo() {
        return tipo;
    }

    public void setTipo(NovedadTipo tipo) {
        this.tipo = tipo;
    }

    public NovedadCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(NovedadCategoria categoria) {
        this.categoria = categoria;
    }

    public Boolean getRequiereAprobacion() {
        return requiereAprobacion;
    }

    public void setRequiereAprobacion(Boolean requiereAprobacion) {
        this.requiereAprobacion = requiereAprobacion;
    }

    public EstadoAprobacion getEstadoAprobacion() {
        return estadoAprobacion;
    }

    public void setEstadoAprobacion(EstadoAprobacion estadoAprobacion) {
        this.estadoAprobacion = estadoAprobacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public RolCreador getRolCreador() {
        return rolCreador;
    }

    public void setRolCreador(RolCreador rolCreador) {
        this.rolCreador = rolCreador;
    }

    public String getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(String estudianteId) {
        this.estudianteId = estudianteId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public String getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(String aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(LocalDateTime fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public String getComentarioAprobacion() {
        return comentarioAprobacion;
    }

    public void setComentarioAprobacion(String comentarioAprobacion) {
        this.comentarioAprobacion = comentarioAprobacion;
    }
}
