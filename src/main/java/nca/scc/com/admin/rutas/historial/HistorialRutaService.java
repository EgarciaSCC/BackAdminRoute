package nca.scc.com.admin.rutas.historial;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.historial.entity.HistorialRuta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialRutaService {

    private final HistorialRutaRepository repository;

    public HistorialRutaService(HistorialRutaRepository repository) {
        this.repository = repository;
    }

    public HistorialRuta create(HistorialRuta historial) {
        return repository.save(historial);
    }

    public List<HistorialRuta> listAll() {
        return repository.findAll();
    }

    public HistorialRuta getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("HistorialRuta not found: " + id));
    }

    public HistorialRuta update(String id, HistorialRuta historial) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("HistorialRuta not found: " + id);
        }
        historial.setId(id);
        return repository.save(historial);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("HistorialRuta not found: " + id);
        }
        repository.deleteById(id);
    }
}
