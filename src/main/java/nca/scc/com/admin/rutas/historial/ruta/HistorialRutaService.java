package nca.scc.com.admin.rutas.historial.ruta;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.historial.ruta.entity.HistorialRuta;
import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.sede.SedeRepository;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialRutaService {

    private final HistorialRutaRepository repository;
    private final RutaRepository rutaRepository;
    private final SedeRepository sedeRepository;

    public HistorialRutaService(HistorialRutaRepository repository, RutaRepository rutaRepository, SedeRepository sedeRepository) {
        this.repository = repository;
        this.rutaRepository = rutaRepository;
        this.sedeRepository = sedeRepository;
    }

    public HistorialRuta create(HistorialRuta historialRuta) {
        return repository.save(historialRuta);
    }

    public List<HistorialRuta> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role != null && role == Role.ROLE_TRANSPORT && tenant != null) {
            List<Sede> sedes = sedeRepository.findByTransportId(tenant);
            var sedeIds = sedes.stream().map(Sede::getId).toList();
            return repository.findAll().stream().filter(h -> {
                return rutaRepository.findById(h.getRutaId()).map(r -> r.sedeId() != null && sedeIds.contains(r.sedeId())).orElse(false);
            }).collect(Collectors.toList());
        } else if (role != null && role == Role.ROLE_SCHOOL && tenant != null) {
            return repository.findAll().stream().filter(h -> {
                return rutaRepository.findById(h.getRutaId()).map(r -> tenant.equals(r.sedeId())).orElse(false);
            }).collect(Collectors.toList());
        } else {
            return repository.findAll();
        }
    }

    public HistorialRuta getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("HistorialRuta not found: " + id));
    }

    public HistorialRuta update(String id, HistorialRuta historialRuta) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("HistorialRuta not found: " + id);
        }
        historialRuta.setId(id);
        return repository.save(historialRuta);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("HistorialRuta not found: " + id);
        }
        repository.deleteById(id);
    }
}
