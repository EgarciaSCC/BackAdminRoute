package nca.scc.com.admin.rutas.driver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Respuesta GET /api/driver/routes/today. Contrato frontend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverRoutesTodayResponse {

    private String driverId;
    private String driverName;
    private String date;  // ISO YYYY-MM-DD
    private DriverRoutePreview activeRoute;
    private List<DriverRoutePreview> scheduledRoutes;
    private List<DriverRoutePreview> completedRoutes;

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public DriverRoutePreview getActiveRoute() { return activeRoute; }
    public void setActiveRoute(DriverRoutePreview activeRoute) { this.activeRoute = activeRoute; }
    public List<DriverRoutePreview> getScheduledRoutes() { return scheduledRoutes; }
    public void setScheduledRoutes(List<DriverRoutePreview> scheduledRoutes) { this.scheduledRoutes = scheduledRoutes; }
    public List<DriverRoutePreview> getCompletedRoutes() { return completedRoutes; }
    public void setCompletedRoutes(List<DriverRoutePreview> completedRoutes) { this.completedRoutes = completedRoutes; }
}
