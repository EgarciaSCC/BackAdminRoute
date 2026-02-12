package nca.scc.com.admin.rutas.historial.pasajero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nca.scc.com.admin.rutas.historial.enums.TipoEventoPasajero;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "HISTORIAL_PASAJERO",
        indexes = {
                @Index(name = "idx_hist_pasajero_ruta", columnList = "rutaId"),
                @Index(name = "idx_hist_pasajero_pasajero", columnList = "pasajeroId"),
                @Index(name = "idx_hist_pasajero_historial", columnList = "historialRutaId")
        })
public class HistorialPasajero {

    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 36)
    private String historialRutaId;

    @NotBlank
    @Column(nullable = false, length = 36)
    private String rutaId;

    @NotBlank
    @Column(nullable = false, length = 36)
    private String pasajeroId;

    @NotBlank
    @Column(nullable = false)
    private String fecha; // yyyy-MM-dd

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEventoPasajero evento; // PICKUP / DROPOFF

    @NotNull
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 200)
    private String nota;

    public HistorialPasajero() {}

    @PrePersist
    public void ensureId() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    /* Getters & setters */

    public String getId() { return id; }
    public String getHistorialRutaId() { return historialRutaId; }
    public String getRutaId() { return rutaId; }
    public String getPasajeroId() { return pasajeroId; }
    public String getFecha() { return fecha; }
    public TipoEventoPasajero getEvento() { return evento; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNota() { return nota; }

    public void setHistorialRutaId(String historialRutaId) { this.historialRutaId = historialRutaId; }
    public void setRutaId(String rutaId) { this.rutaId = rutaId; }
    public void setPasajeroId(String pasajeroId) { this.pasajeroId = pasajeroId; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setEvento(TipoEventoPasajero evento) { this.evento = evento; }
    public void setNota(String nota) { this.nota = nota; }
}
