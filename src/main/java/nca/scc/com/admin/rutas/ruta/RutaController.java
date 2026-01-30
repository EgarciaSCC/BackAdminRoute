package nca.scc.com.admin.rutas.ruta;

import jakarta.validation.Valid;
import nca.scc.com.admin.rutas.ruta.dto.RutaResponseDTO;
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

    // Devuelve DTOs completos con relaciones
    @GetMapping
    public List<RutaResponseDTO> list() {
        return service.listAllFull();
    }

    @GetMapping("/{id}")
    public RutaResponseDTO get(@PathVariable String id) {
        return service.getFullById(id);
    }

    // Endpoints para compatibilidad: crear/actualizar la entidad Ruta sin resolver relaciones
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
