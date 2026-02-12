package nca.scc.com.admin.rutas.rutaPasajeros.entity;

import jakarta.persistence.*;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.enums.EstadoRutaPasajeros;

import java.time.LocalDateTime;

@Entity
@Table(name = "ruta_pasajeros")
public class RutaPasajero {

    @EmbeddedId
    private RutaPasajeroId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRutaPasajeros estado = EstadoRutaPasajeros.PENDIENTE;

    private LocalDateTime pickupAt;
    private LocalDateTime dropoffAt;

    private Integer orden;

    public RutaPasajero() {}

    public RutaPasajero(String rutaId, String pasajeroId, Integer orden) {
        this.id = new RutaPasajeroId(rutaId, pasajeroId);
        this.orden = orden;
    }


    public void pickup() {
        if (estado != EstadoRutaPasajeros.PENDIENTE) {
            throw new IllegalStateException("Pickup no permitido");
        }
        this.estado = EstadoRutaPasajeros.PICKED_UP;
        this.pickupAt = LocalDateTime.now();
    }

    public void dropoff() {
        if (estado != EstadoRutaPasajeros.PICKED_UP) {
            throw new IllegalStateException("Dropoff no permitido");
        }
        this.estado = EstadoRutaPasajeros.DROPPED_OFF;
        this.dropoffAt = LocalDateTime.now();
    }

    public RutaPasajero(RutaPasajeroId id, EstadoRutaPasajeros estado) {
        this.id = id;
        this.estado = estado;
    }

    public RutaPasajeroId getId() {
        return id;
    }

    public EstadoRutaPasajeros getEstado() {
        return estado;
    }
    public LocalDateTime getPickupAt() {
        return pickupAt;
    }

    public LocalDateTime getDropoffAt() {
        return dropoffAt;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public void setEstado(EstadoRutaPasajeros estado) {
        this.estado = estado;
    }

    public void setPickupAt(LocalDateTime pickupAt) {
        this.pickupAt = pickupAt;
    }

    public void setDropoffAt(LocalDateTime dropoffAt) {
        this.dropoffAt = dropoffAt;
    }

    public void setId(RutaPasajeroId id) {
        this.id = id;
    }
}

