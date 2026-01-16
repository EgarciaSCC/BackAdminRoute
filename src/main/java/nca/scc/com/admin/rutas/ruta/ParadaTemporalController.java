package nca.scc.com.admin.rutas.ruta;

import nca.scc.com.admin.rutas.ruta.dto.ParadaTemporalDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rutas/{rutaId}/paradas-temporales")
public class ParadaTemporalController {

    private final RutaService rutaService;

    public ParadaTemporalController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@PathVariable String rutaId, @RequestBody ParadaTemporalDTO body) {
        ParadaTemporalDTO created = rutaService.createParadaTemporal(rutaId, body);
        return success(created);
    }

    @GetMapping
    public Map<String, Object> list(@PathVariable String rutaId) {
        List<ParadaTemporalDTO> list = rutaService.listParadasTemporales(rutaId);
        return success(list);
    }

    @PutMapping("/{paradaId}/approve")
    public Map<String, Object> approve(@PathVariable String rutaId, @PathVariable String paradaId, @RequestBody Map<String, String> body) {
        String aprobadoPor = body.get("aprobadoPor");
        ParadaTemporalDTO p = rutaService.approveParadaTemporal(rutaId, paradaId, aprobadoPor);
        Map<String, Object> data = new HashMap<>();
        data.put("approved", true);
        data.put("parada", p);
        return success(data);
    }

    @PutMapping("/{paradaId}/reject")
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
