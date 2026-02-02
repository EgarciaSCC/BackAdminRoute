package nca.scc.com.admin.rutas.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Usuario expuesto en respuestas de login y validate.
 * Coincide con el contrato frontend: id, username, role ('driver'|'admin'|'parent'), name, email.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String id;
    private String username;
    private String role;  // "driver" | "admin" | "parent"
    private String name;
    private String email;

    public UserDto() {}

    public UserDto(String id, String username, String role, String name, String email) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
