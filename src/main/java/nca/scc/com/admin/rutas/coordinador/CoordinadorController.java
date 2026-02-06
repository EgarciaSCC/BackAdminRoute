package nca.scc.com.admin.rutas.coordinador;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para gestión de Coordinadores
 *
 * Endpoints:
 * - GET /api/coordinadores (listar todos)
 * - GET /api/coordinadores/{id} (obtener por ID)
 * - GET /api/coordinadores/estado/activos (listar activos)
 * - POST /api/coordinadores (crear)
 * - PUT /api/coordinadores/{id} (actualizar)
 * - DELETE /api/coordinadores/{id} (eliminar)
 */
@RestController
@RequestMapping("/api/coordinadores")
public class CoordinadorController {

    private static final Logger log = LoggerFactory.getLogger(CoordinadorController.class);
    private final CoordinadorService service;

    public CoordinadorController(CoordinadorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Coordinador> list() {
        log.debug("GET /api/coordinadores");
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Coordinador get(@PathVariable String id) {
        log.debug("GET /api/coordinadores/{}", id);
        return service.getById(id);
    }

    @GetMapping("/estado/activos")
    public List<Coordinador> listActivos() {
        log.debug("GET /api/coordinadores/estado/activos");
        return service.listActivos();
    }

    @PostMapping
    public ResponseEntity<Coordinador> create(@Valid @RequestBody Coordinador coordinador) {
        log.info("POST /api/coordinadores - Creando coordinador: {}", coordinador.getNombre());
        Coordinador created = service.create(coordinador);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public Coordinador update(@PathVariable String id, @Valid @RequestBody Coordinador coordinador) {
        log.info("PUT /api/coordinadores/{} - Actualizando coordinador", id);
        return service.update(id, coordinador);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        log.info("DELETE /api/coordinadores/{} - Eliminando coordinador", id);
        service.delete(id);
    }
}
