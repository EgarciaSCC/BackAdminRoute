package nca.scc.com.admin.rutas.sede;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeService service;

    public SedeController(SedeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Sede> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Sede get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Sede create(@Valid @RequestBody Sede sede) {
        return service.create(sede);
    }

    @PutMapping("/{id}")
    public Sede update(@PathVariable String id, @Valid @RequestBody Sede sede) {
        return service.update(id, sede);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
