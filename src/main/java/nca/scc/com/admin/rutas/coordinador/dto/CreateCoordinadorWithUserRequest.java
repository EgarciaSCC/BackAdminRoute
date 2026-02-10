package nca.scc.com.admin.rutas.coordinador.dto;

import nca.scc.com.admin.rutas.coordinador.entity.enums.CoordinadorState;
import jakarta.validation.constraints.*;

/**
 * DTO para crear un Coordinador con su Usuario asociado en un único paso
 * Simplifica el proceso de onboarding de coordinadores
 */
public class CreateCoordinadorWithUserRequest {

    @NotBlank(message = "Nombre del coordinador es requerido")
    private String nombre;

    @NotBlank(message = "Cédula es requerida")
    private String cedula;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    // Datos del usuario asociado
    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "Contraseña es requerida")
    @Size(min = 6, max = 72, message = "Contraseña debe tener entre 6 y 72 caracteres")
    private String password;

    @NotNull(message = "Estado del coordinador es requerido")
    private CoordinadorState estado;

    private String tenant;
    private String telefono;

    // Constructores
    public CreateCoordinadorWithUserRequest() {}

    public CreateCoordinadorWithUserRequest(String nombre, String cedula, String email,
                                           String username, String password,
                                           CoordinadorState estado, String tenant) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.email = email;
        this.username = username;
        this.password = password;
        this.estado = estado;
        this.tenant = tenant;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public CoordinadorState getEstado() { return estado; }
    public void setEstado(CoordinadorState estado) { this.estado = estado; }
    public String getTenant() { return tenant; }
    public void setTenant(String tenant) { this.tenant = tenant; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
