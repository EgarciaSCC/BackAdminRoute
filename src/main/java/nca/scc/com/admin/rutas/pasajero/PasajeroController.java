package nca.scc.com.admin.rutas.pasajero;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.pasajero.dto.PasajeroPublicDTO;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;

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
    public List<PasajeroPublicDTO> list() {
        log.debug("GET /api/pasajeros");
        Role role = SecurityUtils.getRoleClaim();
        return service.listAll().stream().map(p -> PasajeroPublicDTO.from(p, role)).toList();
    }

    @GetMapping("/{id}")
    public PasajeroPublicDTO get(@PathVariable String id) {
        log.debug("GET /api/pasajeros/{}", id);
        Role role = SecurityUtils.getRoleClaim();
        return PasajeroPublicDTO.from(service.getById(id), role);
    }

    @GetMapping("/sede/{sedeId}")
    public List<PasajeroPublicDTO> listBySede(@PathVariable String sedeId) {
        log.debug("GET /api/pasajeros/sede/{}", sedeId);
        Role role = SecurityUtils.getRoleClaim();
        return service.listBySedeId(sedeId).stream().map(p -> PasajeroPublicDTO.from(p, role)).toList();
    }

    @GetMapping("/estado/activos")
    public List<PasajeroPublicDTO> listActivos() {
        log.debug("GET /api/pasajeros/estado/activos");
        Role role = SecurityUtils.getRoleClaim();
        return service.listActivos().stream().map(p -> PasajeroPublicDTO.from(p, role)).toList();
    }

    @PostMapping
    public ResponseEntity<PasajeroPublicDTO> create(@Valid @RequestBody Pasajero pasajero) {
        log.info("POST /api/pasajeros - Creando pasajero: {}", pasajero.getNombre());
        Pasajero created = service.create(pasajero);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        Role role = SecurityUtils.getRoleClaim();
        return ResponseEntity.created(location).body(PasajeroPublicDTO.from(created, role));
    }

    @PutMapping("/{id}")
    public PasajeroPublicDTO update(@PathVariable String id, @Valid @RequestBody Pasajero pasajero) {
        log.info("PUT /api/pasajeros/{} - Actualizando pasajero", id);
        Role role = SecurityUtils.getRoleClaim();
        Pasajero updated = service.update(id, pasajero);
        return PasajeroPublicDTO.from(updated, role);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        log.info("DELETE /api/pasajeros/{} - Eliminando pasajero", id);
        service.delete(id);
    }
}
