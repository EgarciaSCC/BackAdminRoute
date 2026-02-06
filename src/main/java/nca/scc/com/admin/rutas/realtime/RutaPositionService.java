package nca.scc.com.admin.rutas.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RutaPositionService {

    private static final Logger log = LoggerFactory.getLogger(RutaPositionService.class);
    private final RutaPositionRepository repository;

    public RutaPositionService(RutaPositionRepository repository) {
        this.repository = repository;
    }

    public RutaPosition save(RutaPosition pos) {
        RutaPosition saved = repository.save(pos);
        log.debug("Saved position for ruta {}: {},{}", pos.getRutaId(), pos.getLat(), pos.getLng());
        return saved;
    }

    public List<RutaPosition> history(String rutaId, int limit) {
        return repository.findByRutaIdOrderByCreatedAtDesc(rutaId).stream().limit(limit).toList();
    }

    public Optional<RutaPosition> last(String rutaId) {
        return repository.findLastPosition(rutaId);
    }
}
