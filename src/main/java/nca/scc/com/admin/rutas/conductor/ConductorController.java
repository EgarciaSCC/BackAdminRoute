package nca.scc.com.admin.rutas.conductor;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import nca.scc.com.admin.rutas.conductor.entity.enums.ConductorState;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para gestión de Conductores
 *
 * Endpoints:
 * - GET /api/conductores (listar todos)
 * - GET /api/conductores/{id} (obtener por ID)
 * - GET /api/conductores/estado/disponibles (listar disponibles)
 * - GET /api/conductores/estado/activos (listar activos)
 * - POST /api/conductores (crear)
 * - PUT /api/conductores/{id} (actualizar)
 * - PUT /api/conductores/{id}/estado (cambiar estado)
 * - DELETE /api/conductores/{id} (eliminar)
 */
@RestController
@RequestMapping("/api/conductores")
public class ConductorController {

    private static final Logger log = LoggerFactory.getLogger(ConductorController.class);
    private final ConductorService service;

    public ConductorController(ConductorService service) {
        this.service = service;
    }

    /**
     * GET /api/conductores
     * Listar todos los conductores del tenant
     */
    @GetMapping
    public List<Conductor> list() {
        log.debug("GET /api/conductores");
        return service.listAll();
    }

    /**
     * GET /api/conductores/{id}
     * Obtener conductor por ID
     */
    @GetMapping("/{id}")
    public Conductor get(@PathVariable String id) {
        log.debug("GET /api/conductores/{}", id);
        return service.getById(id);
    }

    /**
     * GET /api/conductores/estado/disponibles
     * Listar conductores disponibles
     */
    @GetMapping("/estado/disponibles")
    public List<Conductor> listDisponibles() {
        log.debug("GET /api/conductores/estado/disponibles");
        return service.listDisponibles();
    }

    /**
     * GET /api/conductores/estado/activos
     * Listar conductores activos
     */
    @GetMapping("/estado/activos")
    public List<Conductor> listActivos() {
        log.debug("GET /api/conductores/estado/activos");
        return service.listActivos();
    }

    /**
     * POST /api/conductores
     * Crear nuevo conductor
     */
    @PostMapping
    public ResponseEntity<Conductor> create(@Valid @RequestBody Conductor conductor) {
        log.info("POST /api/conductores - Creando conductor: {}", conductor.getNombre());
        Conductor created = service.create(conductor);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * PUT /api/conductores/{id}
     * Actualizar conductor
     */
    @PutMapping("/{id}")
    public Conductor update(@PathVariable String id, @Valid @RequestBody Conductor conductor) {
        log.info("PUT /api/conductores/{} - Actualizando conductor", id);
        return service.update(id, conductor);
    }

    /**
     * PUT /api/conductores/{id}/estado?nuevoEstado=disponible
     * Cambiar estado del conductor
     */
    @PutMapping("/{id}/estado")
    public Conductor changeState(
            @PathVariable String id,
            @RequestParam ConductorState nuevoEstado) {
        log.info("PUT /api/conductores/{}/estado - Cambiando a: {}", id, nuevoEstado);
        return service.changeState(id, nuevoEstado);
    }

    /**
     * DELETE /api/conductores/{id}
     * Eliminar conductor (soft delete)
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        log.info("DELETE /api/conductores/{} - Eliminando conductor", id);
        service.delete(id);
    }
}
