package nca.scc.com.admin.rutas.bus.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import nca.scc.com.admin.rutas.bus.entity.enums.BusState;
import nca.scc.com.admin.rutas.bus.entity.enums.MotorType;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @Column(length = 36)
    private String id;

    @NotBlank
    private String placa;

    @Min(1)
    private int capacidad;

    @NotBlank
    private String marca;

    private String modelo;

    // Mantengo como String para simplicidad (ISO date expected)
    private String fechaRevisionTecnica;

    private String fechaSeguroObligatorio;

    @NotNull
    private MotorType tipoMotor;

    private String tipoMotorOtro;

    @NotNull
    private BusState estado;

    public Bus() {
    }

    @JsonCreator
    public Bus(@JsonProperty("id") String id,
               @JsonProperty("placa") String placa,
               @JsonProperty("capacidad") int capacidad,
               @JsonProperty("marca") String marca,
               @JsonProperty("modelo") String modelo,
               @JsonProperty("fechaRevisionTecnica") String fechaRevisionTecnica,
               @JsonProperty("fechaSeguroObligatorio") String fechaSeguroObligatorio,
               @JsonProperty("tipoMotor") MotorType tipoMotor,
               @JsonProperty("tipoMotorOtro") String tipoMotorOtro,
               @JsonProperty("estado") BusState estado) {
        this.id = id;
        this.placa = placa;
        this.capacidad = capacidad;
        this.marca = marca;
        this.modelo = modelo;
        this.fechaRevisionTecnica = fechaRevisionTecnica;
        this.fechaSeguroObligatorio = fechaSeguroObligatorio;
        this.tipoMotor = tipoMotor;
        this.tipoMotorOtro = tipoMotorOtro;
        this.estado = estado;
    }

    @PrePersist
    public void ensureId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFechaRevisionTecnica() {
        return fechaRevisionTecnica;
    }

    public void setFechaRevisionTecnica(String fechaRevisionTecnica) {
        this.fechaRevisionTecnica = fechaRevisionTecnica;
    }

    public String getFechaSeguroObligatorio() {
        return fechaSeguroObligatorio;
    }

    public void setFechaSeguroObligatorio(String fechaSeguroObligatorio) {
        this.fechaSeguroObligatorio = fechaSeguroObligatorio;
    }

    public MotorType getTipoMotor() {
        return tipoMotor;
    }

    public void setTipoMotor(MotorType tipoMotor) {
        this.tipoMotor = tipoMotor;
    }

    public String getTipoMotorOtro() {
        return tipoMotorOtro;
    }

    public void setTipoMotorOtro(String tipoMotorOtro) {
        this.tipoMotorOtro = tipoMotorOtro;
    }

    public BusState getEstado() {
        return estado;
    }

    public void setEstado(BusState estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bus)) return false;
        Bus bus = (Bus) o;
        return capacidad == bus.capacidad && Objects.equals(id, bus.id) && Objects.equals(placa, bus.placa) && Objects.equals(marca, bus.marca) && Objects.equals(modelo, bus.modelo) && Objects.equals(fechaRevisionTecnica, bus.fechaRevisionTecnica) && Objects.equals(fechaSeguroObligatorio, bus.fechaSeguroObligatorio) && tipoMotor == bus.tipoMotor && Objects.equals(tipoMotorOtro, bus.tipoMotorOtro) && estado == bus.estado;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, placa, capacidad, marca, modelo, fechaRevisionTecnica, fechaSeguroObligatorio, tipoMotor, tipoMotorOtro, estado);
    }

}
