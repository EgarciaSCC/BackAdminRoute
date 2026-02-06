package nca.scc.com.admin.rutas.realtime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ruta_position", indexes = {
    @Index(name = "idx_ruta_position_ruta_id", columnList = "ruta_id"),
    @Index(name = "idx_ruta_position_created_at", columnList = "created_at")
})
public class RutaPosition {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    @Column(name = "ruta_id", length = 36, nullable = false)
    private String rutaId;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    private Integer heading;
    private Integer speed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RutaPosition() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public RutaPosition(String rutaId, Double lat, Double lng, Integer heading, Integer speed) {
        this.id = UUID.randomUUID().toString();
        this.rutaId = rutaId;
        this.lat = lat;
        this.lng = lng;
        this.heading = heading;
        this.speed = speed;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRutaId() { return rutaId; }
    public void setRutaId(String rutaId) { this.rutaId = rutaId; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public Integer getHeading() { return heading; }
    public void setHeading(Integer heading) { this.heading = heading; }
    public Integer getSpeed() { return speed; }
    public void setSpeed(Integer speed) { this.speed = speed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
