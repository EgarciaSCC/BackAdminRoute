package nca.scc.com.admin.rutas.conductor.dto;

import nca.scc.com.admin.rutas.conductor.entity.enums.ConductorState;
import jakarta.validation.constraints.*;

/**
 * DTO para crear un Conductor con su Usuario asociado en un único paso
 * Simplifica el proceso de onboarding de conductores
 */
public class CreateConductorWithUserRequest {

    @NotBlank(message = "Nombre del conductor es requerido")
    private String nombre;

    @NotBlank(message = "Cédula es requerida")
    private String cedula;

    @NotBlank(message = "Número de licencia es requerido")
    private String licencia;

    @NotBlank(message = "Tipo de licencia es requerido")
    private String tipoLicencia;

    // Datos del usuario asociado
    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "Contraseña es requerida")
    @Size(min = 6, max = 72, message = "Contraseña debe tener entre 6 y 72 caracteres")
    private String password;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    @NotNull(message = "Estado del conductor es requerido")
    private ConductorState estado;

    private String tenant;
    private String telefonoEmergencia;

    // Constructores
    public CreateConductorWithUserRequest() {}

    public CreateConductorWithUserRequest(String nombre, String cedula, String licencia,
                                         String tipoLicencia, String username, String password,
                                         String email, ConductorState estado, String tenant) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.licencia = licencia;
        this.tipoLicencia = tipoLicencia;
        this.username = username;
        this.password = password;
        this.email = email;
        this.estado = estado;
        this.tenant = tenant;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    public String getTipoLicencia() { return tipoLicencia; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public ConductorState getEstado() { return estado; }
    public void setEstado(ConductorState estado) { this.estado = estado; }
    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }
    public String getTelefonoEmergencia() { return telefonoEmergencia; }
    public void setTelefonoEmergencia(String telefonoEmergencia) { this.telefonoEmergencia = telefonoEmergencia; }
}
