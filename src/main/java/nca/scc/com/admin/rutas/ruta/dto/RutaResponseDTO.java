package nca.scc.com.admin.rutas.ruta.dto;

import nca.scc.com.admin.rutas.bus.entity.Bus;
import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;

import java.util.List;

public class RutaResponseDTO {
    private Ruta ruta;
    private Bus bus;
    private Conductor conductor;
    private Coordinador coordinador;
    private Sede sede;
    private List<Pasajero> pasajeros;

    public RutaResponseDTO(Ruta ruta, Bus bus, Conductor conductor, Coordinador coordinador, Sede sede, List<Pasajero> pasajeros) {
        this.ruta = ruta;
        this.bus = bus;
        this.conductor = conductor;
        this.coordinador = coordinador;
        this.sede = sede;
        this.pasajeros = pasajeros;
    }

    public RutaResponseDTO() {}

    public Ruta getRuta() { return ruta; }
    public void setRuta(Ruta ruta) { this.ruta = ruta; }
    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }
    public Conductor getConductor() { return conductor; }
    public void setConductor(Conductor conductor) { this.conductor = conductor; }
    public Coordinador getCoordinador() { return coordinador; }
    public void setCoordinador(Coordinador coordinador) { this.coordinador = coordinador; }
    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }
    public List<Pasajero> getPasajeros() { return pasajeros; }
    public void setPasajeros(List<Pasajero> pasajeros) { this.pasajeros = pasajeros; }
}
