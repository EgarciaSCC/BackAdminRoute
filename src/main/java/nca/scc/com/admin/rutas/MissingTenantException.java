package nca.scc.com.admin.rutas;

public class MissingTenantException extends RuntimeException {
    public MissingTenantException(String message) {
        super(message);
    }
}
