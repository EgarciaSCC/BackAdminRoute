package nca.scc.com.admin.rutas.ruta.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParadaTemporalDTO {
    private String id;
    private String estudianteId;
    private String direccion;
    private Double lat;
    private Double lng;
    private String motivo;
    private String creadoPor;
    private String rolCreador;
    private Instant createdAt;
    private Instant expiraAt;
    private String estado; // pendiente | aprobada | rechazada
    private String aprobadoPor;
    private Instant fechaAprobacion;
    private String comentario;

    public ParadaTemporalDTO() {
        this.id = "pt-" + UUID.randomUUID().toString();
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEstudianteId() { return estudianteId; }
    public void setEstudianteId(String estudianteId) { this.estudianteId = estudianteId; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
    public String getRolCreador() { return rolCreador; }
    public void setRolCreador(String rolCreador) { this.rolCreador = rolCreador; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getExpiraAt() { return expiraAt; }
    public void setExpiraAt(Instant expiraAt) { this.expiraAt = expiraAt; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getAprobadoPor() { return aprobadoPor; }
    public void setAprobadoPor(String aprobadoPor) { this.aprobadoPor = aprobadoPor; }
    public Instant getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(Instant fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
