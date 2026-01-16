package nca.scc.com.admin.rutas.bus;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.bus.entity.Bus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService service;

    public BusController(BusService service) {
        this.service = service;
    }

    @GetMapping
    public List<Bus> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Bus get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Bus> create(@Valid @RequestBody Bus bus) {
        Bus created = service.create(bus);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public Bus update(@PathVariable String id, @Valid @RequestBody Bus bus) {
        return service.update(id, bus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}

