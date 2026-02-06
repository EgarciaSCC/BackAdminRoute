package nca.scc.com.admin.rutas.novedad;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.novedad.entity.Novedad;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novedades")
@Tag(name = "Novedades", description = "News and incident management")
public class NovedadController {

    private final NovedadService service;

    public NovedadController(NovedadService service) {
        this.service = service;
    }

    @GetMapping
    public List<Novedad> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Novedad get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Novedad create(@Valid @RequestBody Novedad novedad) {
        return service.create(novedad);
    }

    @PutMapping("/{id}")
    public Novedad update(@PathVariable String id, @Valid @RequestBody Novedad novedad) {
        return service.update(id, novedad);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve novelty", description = "Approve a novelty record")
    @ApiResponse(responseCode = "200", description = "Novelty approved", content = @Content(schema = @Schema(type = "object")))
    public Map<String, Object> approve(@PathVariable String id, @RequestBody Map<String, String> body) {
        String aprobadoPor = body.get("aprobadoPor");
        String comentario = body.get("comentario");
        Novedad n = service.approveNovedad(id, aprobadoPor, comentario);
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("data", n);
        return r;
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject novelty", description = "Reject a novelty record")
    @ApiResponse(responseCode = "200", description = "Novelty rejected", content = @Content(schema = @Schema(type = "object")))
    public Map<String, Object> reject(@PathVariable String id, @RequestBody Map<String, String> body) {
        String aprobadoPor = body.get("aprobadoPor");
        String comentario = body.get("comentario");
        Novedad n = service.rejectNovedad(id, aprobadoPor, comentario);
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("data", n);
        return r;
    }
}
