package nca.scc.com.admin.rutas.driver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;

import java.util.List;

/**
 * Respuesta GET /api/driver/routes/today. Contrato frontend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverRouteHome {

    private String driverId;
    private String driverName;
    private String date;  // ISO YYYY-MM-DD
    private DriverRoutePreview activeRoute;
    private List<Ruta> activeRoutes;
    private List<Ruta> scheduledRoutes;
    private List<Ruta> completedRoutes;

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<Ruta> getActiveRoutes() { return activeRoutes; }
    public void setActiveRoutes(List<Ruta> activeRoutes) { this.activeRoutes = activeRoutes; }
    public List<Ruta> getScheduledRoutes() { return scheduledRoutes; }
    public void setScheduledRoutes(List<Ruta> scheduledRoutes) { this.scheduledRoutes = scheduledRoutes; }
    public List<Ruta> getCompletedRoutes() { return completedRoutes; }
    public void setCompletedRoutes(List<Ruta> completedRoutes) { this.completedRoutes = completedRoutes; }
}
