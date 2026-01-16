package nca.scc.com.admin.rutas.ruta;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService service;

    public RutaController(RutaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Ruta> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Ruta get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Ruta create(@Valid @RequestBody Ruta ruta) {
        return service.create(ruta);
    }

    @PutMapping("/{id}")
    public Ruta update(@PathVariable String id, @Valid @RequestBody Ruta ruta) {
        return service.update(id, ruta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
