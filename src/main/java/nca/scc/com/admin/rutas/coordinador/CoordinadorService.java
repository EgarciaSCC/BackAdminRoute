package nca.scc.com.admin.rutas.coordinador;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoordinadorService {

    private final CoordinadorRepository repository;

    public CoordinadorService(CoordinadorRepository repository) {
        this.repository = repository;
    }

    public Coordinador create(Coordinador coordinador) {
        return repository.save(coordinador);
    }

    public List<Coordinador> listAll() {
        return repository.findAll();
    }

    public Coordinador getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coordinador not found: " + id));
    }

    public Coordinador update(String id, Coordinador coordinador) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Coordinador not found: " + id);
        }
        coordinador.setId(id);
        return repository.save(coordinador);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Coordinador not found: " + id);
        }
        repository.deleteById(id);
    }
}
