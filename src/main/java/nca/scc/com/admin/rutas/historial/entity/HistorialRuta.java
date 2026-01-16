package nca.scc.com.admin.rutas.historial.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nca.scc.com.admin.rutas.historial.entity.enums.EstadoHistorialRuta;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HISTORIAL_RUTA")
public class HistorialRuta {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    @Column(nullable = false, length = 36)
    private String rutaId;

    @NotBlank
    @Column(nullable = false)
    private String fecha; // ISO: yyyy-MM-dd

    @NotBlank
    @Column(nullable = false)
    private String horaInicio; // HH:mm

    @NotBlank
    @Column(nullable = false)
    private String horaFin; // HH:mm

    @Min(0)
    @Column(nullable = false)
    private int estudiantesRecogidos;

    @Min(0)
    @Column(nullable = false)
    private int estudiantesTotales;

    @Min(0)
    @Column(nullable = false)
    private double kmRecorridos;

    @ElementCollection
    @CollectionTable(
            name = "HISTORIAL_NOVEDADES",
            joinColumns = @JoinColumn(name = "historial_id")
    )
    @Column(name = "novedad_id", length = 36)
    private List<String> novedades = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHistorialRuta estado;

    /* ======================
       Constructors
       ====================== */

    public HistorialRuta() {
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public int getEstudiantesRecogidos() {
        return estudiantesRecogidos;
    }

    public void setEstudiantesRecogidos(int estudiantesRecogidos) {
        this.estudiantesRecogidos = estudiantesRecogidos;
    }

    public int getEstudiantesTotales() {
        return estudiantesTotales;
    }

    public void setEstudiantesTotales(int estudiantesTotales) {
        this.estudiantesTotales = estudiantesTotales;
    }

    public double getKmRecorridos() {
        return kmRecorridos;
    }

    public void setKmRecorridos(double kmRecorridos) {
        this.kmRecorridos = kmRecorridos;
    }

    public List<String> getNovedades() {
        return novedades;
    }

    public void setNovedades(List<String> novedades) {
        this.novedades = novedades;
    }

    public EstadoHistorialRuta getEstado() {
        return estado;
    }

    public void setEstado(EstadoHistorialRuta estado) {
        this.estado = estado;
    }
}
