package nca.scc.com.admin.rutas.ruta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import nca.scc.com.admin.rutas.ruta.dto.ParadaTemporalDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rutas/{rutaId}/paradas-temporales")
@Tag(name = "Paradas Temporales", description = "Temporary stops management")
public class ParadaTemporalController {

    private final RutaService rutaService;

    public ParadaTemporalController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create temporary stop", description = "Create a new temporary stop for a route")
    @ApiResponse(responseCode = "201", description = "Stop created", content = @Content(schema = @Schema(type = "object")))
    public Map<String, Object> create(@PathVariable String rutaId, @RequestBody ParadaTemporalDTO body) {
        ParadaTemporalDTO created = rutaService.createParadaTemporal(rutaId, body);
        return success(created);
    }

    @GetMapping
    @Operation(summary = "List temporary stops", description = "Get all temporary stops for a route")
    @ApiResponse(responseCode = "200", description = "List of stops", content = @Content(schema = @Schema(type = "object")))
    public Map<String, Object> list(@PathVariable String rutaId) {
        List<ParadaTemporalDTO> list = rutaService.listParadasTemporales(rutaId);
        return success(list);
    }

    @PutMapping("/{paradaId}/approve")
    @Operation(summary = "Approve temporary stop", description = "Approve a temporary stop")
    @ApiResponse(responseCode = "200", description = "Stop approved", content = @Content(schema = @Schema(type = "object")))
    public Map<String, Object> approve(@PathVariable String rutaId, @PathVariable String paradaId, @RequestBody Map<String, String> body) {
        String aprobadoPor = body.get("aprobadoPor");
        ParadaTemporalDTO p = rutaService.approveParadaTemporal(rutaId, paradaId, aprobadoPor);
        Map<String, Object> data = new HashMap<>();
        data.put("approved", true);
        data.put("parada", p);
        return success(data);
    }

    @PutMapping("/{paradaId}/reject")
    @Operation(summary = "Reject temporary stop", description = "Reject a temporary stop")
    @ApiResponse(responseCode = "200", description = "Stop rejected", content = @Content(schema = @Schema(type = "object")))
    public Map<String, Object> reject(@PathVariable String rutaId, @PathVariable String paradaId, @RequestBody Map<String, String> body) {
        String aprobadoPor = body.get("aprobadoPor");
        String comentario = body.get("comentario");
        ParadaTemporalDTO p = rutaService.rejectParadaTemporal(rutaId, paradaId, aprobadoPor, comentario);
        Map<String, Object> data = new HashMap<>();
        data.put("rejected", true);
        data.put("parada", p);
        return success(data);
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("data", data);
        return r;
    }
}
