package nca.scc.com.admin.rutas.auth;

/**
 * Roles internos del backend. Para la API se exponen como 'driver'|'admin'|'parent'.
 */
public enum Role {
    ROLE_TRANSPORT,
    ROLE_SCHOOL,
    ROLE_ADMIN;

    /** Mapeo al contrato frontend: driver, admin, parent */
    public String toFrontendRole() {
        return switch (this) {
            case ROLE_TRANSPORT -> "driver";
            case ROLE_SCHOOL -> "parent";
            case ROLE_ADMIN -> "admin";
        };
    }
}
