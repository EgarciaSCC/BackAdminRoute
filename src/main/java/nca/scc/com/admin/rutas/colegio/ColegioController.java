package nca.scc.com.admin.rutas.colegio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nca.scc.com.admin.rutas.colegio.entity.Colegio;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/colegios")
@Tag(name = "Colegios", description = "Gestión de colegios")
public class ColegioController {

    private final ColegioService service;

    public ColegioController(ColegioService service) {
        this.service = service;
    }

    /**
     * Crear nuevo colegio
     */
    @PostMapping
    @Operation(summary = "Crear colegio", description = "Crea un nuevo colegio en el tenant actual")
    @ApiResponse(responseCode = "201", description = "Colegio creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o NIT duplicado")
    public ResponseEntity<Colegio> create(@Valid @RequestBody Colegio colegio) {
        Colegio created = service.create(colegio);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Listar todos los colegios del tenant
     */
    @GetMapping
    @Operation(summary = "Listar colegios", description = "Obtiene todos los colegios del tenant actual")
    @ApiResponse(responseCode = "200", description = "Lista de colegios")
    public ResponseEntity<List<Colegio>> listAll() {
        List<Colegio> colegios = service.listAll();
        return ResponseEntity.ok(colegios);
    }

    /**
     * Obtener colegio por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener colegio", description = "Obtiene los detalles de un colegio específico")
    @ApiResponse(responseCode = "200", description = "Colegio encontrado")
    @ApiResponse(responseCode = "404", description = "Colegio no encontrado")
    public ResponseEntity<Colegio> getById(@PathVariable String id) {
        Colegio colegio = service.getById(id);
        return ResponseEntity.ok(colegio);
    }

    /**
     * Actualizar colegio
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar colegio", description = "Actualiza un colegio existente")
    @ApiResponse(responseCode = "200", description = "Colegio actualizado")
    @ApiResponse(responseCode = "404", description = "Colegio no encontrado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<Colegio> update(@PathVariable String id, @Valid @RequestBody Colegio colegio) {
        Colegio updated = service.update(id, colegio);
        return ResponseEntity.ok(updated);
    }

    /**
     * Eliminar colegio (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar colegio", description = "Elimina un colegio (soft delete)")
    @ApiResponse(responseCode = "204", description = "Colegio eliminado")
    @ApiResponse(responseCode = "404", description = "Colegio no encontrado")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Listar colegios activos
     */
    @GetMapping("/activos/lista")
    @Operation(summary = "Listar colegios activos", description = "Obtiene solo los colegios activos del tenant")
    @ApiResponse(responseCode = "200", description = "Lista de colegios activos")
    public ResponseEntity<List<Colegio>> listActivos() {
        List<Colegio> colegios = service.listActivos();
        return ResponseEntity.ok(colegios);
    }
}
