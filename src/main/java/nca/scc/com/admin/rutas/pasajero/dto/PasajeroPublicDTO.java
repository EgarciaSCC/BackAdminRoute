package nca.scc.com.admin.rutas.pasajero.dto;

import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.auth.Role;

public class PasajeroPublicDTO {
    private String id;
    private String nombre;
    private String matricula;
    private String curso;
    private String direccion;
    private String barrio;
    private Double lat;
    private Double lng;
    private String telefonoEmergencia; // visible solo para transport/admin

    public PasajeroPublicDTO() {}

    public static PasajeroPublicDTO from(Pasajero p, Role role) {
        PasajeroPublicDTO d = new PasajeroPublicDTO();
        d.id = p.getId();
        d.nombre = p.getNombre();
        d.matricula = p.getMatricula();
        d.curso = p.getCurso();
        d.direccion = p.getDireccion();
        d.barrio = p.getBarrio();
        d.lat = p.getLat();
        d.lng = p.getLng();
        if (role == Role.ROLE_ADMIN || role == Role.ROLE_TRANSPORT || role == Role.ROLE_SCHOOL) {
            d.telefonoEmergencia = p.getTelefonoEmergencia();
        } else {
            d.telefonoEmergencia = null;
        }
        return d;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public String getTelefonoEmergencia() { return telefonoEmergencia; }
    public void setTelefonoEmergencia(String telefonoEmergencia) { this.telefonoEmergencia = telefonoEmergencia; }
}
