package nca.scc.com.admin.rutas.pasajero;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasajeroService {

    private final PasajeroRepository repository;

    public PasajeroService(PasajeroRepository repository) {
        this.repository = repository;
    }

    public Pasajero create(Pasajero pasajero) {
        return repository.save(pasajero);
    }

    public List<Pasajero> listAll() {
        return repository.findAll();
    }

    public Pasajero getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pasajero not found: " + id));
    }

    public Pasajero update(String id, Pasajero pasajero) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Pasajero not found: " + id);
        }
        pasajero.setId(id);
        return repository.save(pasajero);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Pasajero not found: " + id);
        }
        repository.deleteById(id);
    }
}
