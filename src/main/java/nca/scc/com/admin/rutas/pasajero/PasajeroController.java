package nca.scc.com.admin.rutas.pasajero;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pasajeros")
public class PasajeroController {

    private final PasajeroService service;

    public PasajeroController(PasajeroService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pasajero> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Pasajero get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Pasajero create(@Valid @RequestBody Pasajero pasajero) {
        return service.create(pasajero);
    }

    @PutMapping("/{id}")
    public Pasajero update(@PathVariable String id, @Valid @RequestBody Pasajero pasajero) {
        return service.update(id, pasajero);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
