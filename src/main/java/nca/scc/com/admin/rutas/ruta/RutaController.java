package nca.scc.com.admin.rutas.ruta;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.ruta.dto.RutaResponseDTO;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService service;

    public RutaController(RutaService service) {
        this.service = service;
    }

    // Devuelve DTOs completos con relaciones
    @GetMapping
    public List<RutaResponseDTO> list() {
        return service.listAllFull();
    }

    @GetMapping("/estado/{estado}")
    public List<RutaResponseDTO> listByEstado(@PathVariable String estado) {
        return service.listAllFull().stream().filter(r -> r.getRuta().getEstado() != null && r.getRuta().getEstado().equalsIgnoreCase(estado)).toList();
    }

    @GetMapping("/{id}")
    public RutaResponseDTO get(@PathVariable String id) {
        return service.getFullById(id);
    }

    // Endpoints para compatibilidad: crear/actualizar la entidad Ruta sin resolver relaciones
    @PostMapping
    public ResponseEntity<Ruta> create(@Valid @RequestBody Ruta ruta) {
        Ruta created = service.create(ruta);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Ruta update(@PathVariable String id, @Valid @RequestBody Ruta ruta) {
        return service.update(id, ruta);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Ruta> publish(@PathVariable String id) {
        Ruta published = service.publish(id);
        return ResponseEntity.ok(published);
    }

    /**
     * Asignar estudiante a ruta con validaciones de negocio.
     * POST /api/rutas/{id}/estudiantes/{estudianteId}
     *
     * Validaciones:
     * - Máximo 2 rutas por día (1 RECOGIDA + 1 LLEVADA)
     * - No solapamiento de horarios
     * - Capacidad del bus disponible
     *
     * @return 201 si se asigna exitosamente, 409 si hay conflicto, 400 si hay error de validación
     */
    @PostMapping("/{id}/estudiantes/{estudianteId}")
    public ResponseEntity<?> asignarEstudiante(@PathVariable String id, @PathVariable String estudianteId) {
        try {
            Ruta ruta = service.asignarEstudianteARuta(id, estudianteId);
            return ResponseEntity.status(HttpStatus.CREATED).body(ruta);
        } catch (IllegalStateException e) {
            // Conflicto de negocio (ej: 2 rutas ya asignadas, overlap horario, capacidad agotada)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse("CONFLICT", e.getMessage())
            );
        } catch (Exception e) {
            // Otros errores (estudiante no encontrado, ruta no encontrada, etc.)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("BAD_REQUEST", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    /**
     * DTO para respuestas de error normalizadas
     */
    public static class ErrorResponse {
        public String code;
        public String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
}
