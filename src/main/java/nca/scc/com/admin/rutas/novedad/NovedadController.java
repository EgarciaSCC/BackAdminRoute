package nca.scc.com.admin.rutas.novedad;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.novedad.entity.Novedad;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novedades")
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
