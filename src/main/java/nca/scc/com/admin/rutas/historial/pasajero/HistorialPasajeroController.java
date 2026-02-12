package nca.scc.com.admin.rutas.historial.pasajero;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.historial.dto.DropoffPasajeroRequest;
import nca.scc.com.admin.rutas.historial.dto.PickupPasajeroRequest;
import nca.scc.com.admin.rutas.historial.pasajero.entity.HistorialPasajero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-pasajeros")
public class HistorialPasajeroController {

    private final HistorialPasajeroService service;

    public HistorialPasajeroController(HistorialPasajeroService service) {
        this.service = service;
    }

    /**
     * Registrar pickup de un pasajero
     */
    @PostMapping("/pickup")
    @ResponseStatus(HttpStatus.CREATED)
    public void pickup(@Valid @RequestBody PickupPasajeroRequest request) {
        service.registrarPickup(request);
    }

    /**
     * Registrar dropoff de un pasajero
     */
    @PostMapping("/dropoff")
    @ResponseStatus(HttpStatus.CREATED)
    public void dropoff(@Valid @RequestBody DropoffPasajeroRequest request) {
        service.registrarDropoff(request);
    }

    /**
     * Consultar eventos de una ruta (por fecha)
     */
    @GetMapping("/ruta/{rutaId}/{fecha}")
    public List<HistorialPasajero> getByRuta(
            @PathVariable String rutaId,
            @PathVariable String fecha
    ) {
        return service.getByRutaAndFecha(rutaId, fecha);
    }

    /**
     * Historial de un pasajero
     */
    @GetMapping("/pasajero/{pasajeroId}")
    public List<HistorialPasajero> getByPasajero(
            @PathVariable String pasajeroId
    ) {
        return service.getByPasajero(pasajeroId);
    }
}
