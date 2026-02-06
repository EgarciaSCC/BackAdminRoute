package nca.scc.com.admin.rutas.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import nca.scc.com.admin.rutas.auth.Role;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    private String id;

    private String nombre;

    @Column(unique = true)
    private String username;

    @Column(length = 100)
    private String password;

    private String email;

    /**
     * Tenant/organización asociada. Para ROLE_TRANSPORT será el id de la empresa de transporte; para ROLE_SCHOOL será el id de la sede/colegio.
     */
    private String tenant;

    /**
     * Role del usuario (ej: ROLE_TRANSPORT, ROLE_SCHOOL)
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Id del conductor cuando el usuario tiene rol driver (ROLE_TRANSPORT).
     */
    private String conductorId;

    public Usuario() {
        this.id = UUID.randomUUID().toString();
    }

    public Usuario(String nombre, String username, String password, String tenant) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.tenant = tenant;
    }

    public Usuario(String nombre, String username, String password, String tenant, Role role) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.tenant = tenant;
        this.role = role;
    }

    // getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getConductorId() {
        return conductorId;
    }

    public void setConductorId(String conductorId) {
        this.conductorId = conductorId;
    }
}
