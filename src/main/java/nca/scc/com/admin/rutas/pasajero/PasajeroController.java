package nca.scc.com.admin.rutas.pasajero;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para gestión de Pasajeros/Estudiantes
 *
 * Endpoints:
 * - GET /api/pasajeros (listar todos)
 * - GET /api/pasajeros/{id} (obtener por ID)
 * - GET /api/pasajeros/sede/{sedeId} (listar por sede)
 * - GET /api/pasajeros/estado/activos (listar activos)
 * - POST /api/pasajeros (crear)
 * - PUT /api/pasajeros/{id} (actualizar)
 * - DELETE /api/pasajeros/{id} (eliminar)
 */
@RestController
@RequestMapping("/api/pasajeros")
public class PasajeroController {

    private static final Logger log = LoggerFactory.getLogger(PasajeroController.class);
    private final PasajeroService service;

    public PasajeroController(PasajeroService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pasajero> list() {
        log.debug("GET /api/pasajeros");
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Pasajero get(@PathVariable String id) {
        log.debug("GET /api/pasajeros/{}", id);
        return service.getById(id);
    }

    @GetMapping("/sede/{sedeId}")
    public List<Pasajero> listBySede(@PathVariable String sedeId) {
        log.debug("GET /api/pasajeros/sede/{}", sedeId);
        return service.listBySedeId(sedeId);
    }

    @GetMapping("/estado/activos")
    public List<Pasajero> listActivos() {
        log.debug("GET /api/pasajeros/estado/activos");
        return service.listActivos();
    }

    @PostMapping
    public ResponseEntity<Pasajero> create(@Valid @RequestBody Pasajero pasajero) {
        log.info("POST /api/pasajeros - Creando pasajero: {}", pasajero.getNombre());
        Pasajero created = service.create(pasajero);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public Pasajero update(@PathVariable String id, @Valid @RequestBody Pasajero pasajero) {
        log.info("PUT /api/pasajeros/{} - Actualizando pasajero", id);
        return service.update(id, pasajero);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        log.info("DELETE /api/pasajeros/{} - Eliminando pasajero", id);
        service.delete(id);
    }
}
