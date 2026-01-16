package nca.scc.com.admin.rutas.coordinador;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordinadores")
public class CoordinadorController {

    private final CoordinadorService service;

    public CoordinadorController(CoordinadorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Coordinador> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Coordinador get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Coordinador create(@Valid @RequestBody Coordinador coordinador) {
        return service.create(coordinador);
    }

    @PutMapping("/{id}")
    public Coordinador update(@PathVariable String id, @Valid @RequestBody Coordinador coordinador) {
        return service.update(id, coordinador);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
