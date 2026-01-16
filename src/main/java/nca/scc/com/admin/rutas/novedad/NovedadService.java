package nca.scc.com.admin.rutas.novedad;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.novedad.entity.Novedad;
import nca.scc.com.admin.rutas.novedad.entity.enums.EstadoAprobacion;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NovedadService {

    private final NovedadRepository repository;

    public NovedadService(NovedadRepository repository) {
        this.repository = repository;
    }

    public Novedad create(Novedad novedad) {
        return repository.save(novedad);
    }

    public List<Novedad> listAll() {
        return repository.findAll();
    }

    public Novedad getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Novedad not found: " + id));
    }

    public Novedad update(String id, Novedad novedad) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Novedad not found: " + id);
        }
        novedad.setId(id);
        return repository.save(novedad);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Novedad not found: " + id);
        }
        repository.deleteById(id);
    }

    public Novedad approveNovedad(String id, String aprobadoPor, String comentario) {
        Novedad n = getById(id);
        n.setEstadoAprobacion(EstadoAprobacion.aprobada);
        n.setAprobadoPor(aprobadoPor);
        n.setFechaAprobacion(LocalDateTime.now());
        n.setComentarioAprobacion(comentario);
        return repository.save(n);
    }

    public Novedad rejectNovedad(String id, String aprobadoPor, String comentario) {
        Novedad n = getById(id);
        n.setEstadoAprobacion(EstadoAprobacion.rechazada);
        n.setAprobadoPor(aprobadoPor);
        n.setFechaAprobacion(LocalDateTime.now());
        n.setComentarioAprobacion(comentario);
        return repository.save(n);
    }
}
