package nca.scc.com.admin.rutas.bus;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.bus.entity.Bus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusService {

    private final BusRepository repository;

    public BusService(BusRepository repository) {
        this.repository = repository;
    }

    public Bus create(Bus bus) {
        return repository.save(bus);
    }

    public List<Bus> listAll() {
        return repository.findAll();
    }

    public Bus getById(String id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Bus not found: " + id));
    }

    public Bus update(String id, Bus bus) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Bus not found: " + id);
        }
        bus.setId(id);
        return repository.save(bus);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Bus not found: " + id);
        }
        repository.deleteById(id);
    }
}
