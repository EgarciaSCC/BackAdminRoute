package nca.scc.com.admin.rutas.conductor;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConductorService {

    private final ConductorRepository repository;

    public ConductorService(ConductorRepository repository) {
        this.repository = repository;
    }

    public Conductor create(Conductor conductor) {
        return repository.save(conductor);
    }

    public List<Conductor> listAll() {
        return repository.findAll();
    }

    public Conductor getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conductor not found: " + id));
    }

    public Conductor update(String id, Conductor conductor) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Conductor not found: " + id);
        }
        conductor.setId(id);
        return repository.save(conductor);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Conductor not found: " + id);
        }
        repository.deleteById(id);
    }
}
