package nca.scc.com.admin.rutas.ruta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.ruta.dto.RutaResponseDTO;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@Tag(name = "Rutas", description = "Routes management")
public class RutaController {

    private final RutaService service;

    public RutaController(RutaService service) {
        this.service = service;
    }

    // Devuelve DTOs completos con relaciones
    @GetMapping
    @Operation(summary = "List all routes", description = "Retrieve all routes with full details")
    @ApiResponse(responseCode = "200", description = "List of routes")
    public List<RutaResponseDTO> list() {
        return service.listAllFull();
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "List routes by status", description = "Filter routes by their status")
    @ApiResponse(responseCode = "200", description = "Filtered routes")
    public List<RutaResponseDTO> listByEstado(@PathVariable String estado) {
        return service.listAllFull().stream().filter(r -> r.getRuta().getEstado() != null && r.getRuta().getEstado().equalsIgnoreCase(estado)).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID", description = "Retrieve a specific route with full details")
    @ApiResponse(responseCode = "200", description = "Route details")
    public RutaResponseDTO get(@PathVariable String id) {
        return service.getFullById(id);
    }

    // Endpoints para compatibilidad: crear/actualizar la entidad Ruta sin resolver relaciones
    @PostMapping
    @Operation(summary = "Create route", description = "Create a new route")
    @ApiResponse(responseCode = "201", description = "Route created")
    public ResponseEntity<Ruta> create(@Valid @RequestBody Ruta ruta) {
        Ruta created = service.create(ruta);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update route", description = "Update an existing route")
    @ApiResponse(responseCode = "200", description = "Route updated")
    public Ruta update(@PathVariable String id, @Valid @RequestBody Ruta ruta) {
        return service.update(id, ruta);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish route", description = "Publish a route")
    @ApiResponse(responseCode = "200", description = "Route published")
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
