package nca.scc.com.admin.rutas.conductor;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conductores")
public class ConductorController {

    private final ConductorService service;

    public ConductorController(ConductorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Conductor> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Conductor get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Conductor create(@Valid @RequestBody Conductor conductor) {
        return service.create(conductor);
    }

    @PutMapping("/{id}")
    public Conductor update(@PathVariable String id, @Valid @RequestBody Conductor conductor) {
        return service.update(id, conductor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
