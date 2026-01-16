package nca.scc.com.admin.rutas.historial;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.historial.entity.HistorialRuta;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-rutas")
public class HistorialRutaController {

    private final HistorialRutaService service;

    public HistorialRutaController(HistorialRutaService service) {
        this.service = service;
    }

    @GetMapping
    public List<HistorialRuta> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public HistorialRuta get(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public HistorialRuta create(@Valid @RequestBody HistorialRuta historial) {
        return service.create(historial);
    }

    @PutMapping("/{id}")
    public HistorialRuta update(@PathVariable String id, @Valid @RequestBody HistorialRuta historial) {
        return service.update(id, historial);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
