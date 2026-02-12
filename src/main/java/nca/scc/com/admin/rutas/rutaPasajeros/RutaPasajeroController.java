package nca.scc.com.admin.rutas.rutaPasajeros;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.RutaPasajero;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver-passenger")
public class RutaPasajeroController {
    private final RutaPasajeroService service;

    public RutaPasajeroController(RutaPasajeroService service) {
        this.service = service;
    }

    @PostMapping("/route/passenger-pickup")
    @Operation(summary = "Passenger pickup", description = "El conductor marca el pasajero como recogido en la ruta activa")
    @ApiResponse(responseCode = "200", description = "Passenger picked up")
    public RutaPasajero pickupAtByIdRutaAndIdPasajero(@RequestBody RutaPasajero rutaPasajero) {
        return service.updatePickupAtByIdRutaAndIdPasajero(rutaPasajero);
    }

    @PostMapping("/route/passenger-dropoff")
    @Operation(summary = "Passenger dropoff", description = "El conductor marca el pasajero como dejado en la ruta activa")
    @ApiResponse(responseCode = "200", description = "Passenger dropped off")
    public RutaPasajero dropoffAtByIdRutaAndIdPasajero(@RequestBody RutaPasajero rutaPasajero) {
        return service.updateDropoffAtByIdRutaAndIdPasajero(rutaPasajero);
    }
}
