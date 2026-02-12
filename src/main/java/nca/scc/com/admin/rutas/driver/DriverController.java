package nca.scc.com.admin.rutas.driver;

import nca.scc.com.admin.rutas.driver.dto.DriverRouteHistoryResponse;
import nca.scc.com.admin.rutas.driver.dto.DriverRouteHome;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    /**
     * GET /api/driver/routes/getRoutesToday
     * Rutas del día para el conductor autenticado (JWT), agrupadas en activa, programadas y completadas.
     */
    @GetMapping("/routes/getRoutesToday")
    public DriverRouteHome getRoutesToday() {
        return driverService.getRoutesToday();
    }


    /**
     * POST /api/driver/routes/start
     * Conductor logueado Inicia ruta activa asignada
     */
    @PutMapping("/routes/startRoute")
    public ResponseEntity<Ruta> start(
            @RequestParam(required = true) String rutaId) {
        Ruta started = driverService.start(rutaId);
        return ResponseEntity.ok(started);
    }

    /**
     * GET /api/driver/routes/history
     * Historial de rutas completadas con paginación y filtro por fechas.
     */
    @GetMapping("/routes/history")
    public DriverRouteHistoryResponse getRoutesHistory(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        return driverService.getRoutesHistory(startDate, endDate, page, limit);
    }
}
