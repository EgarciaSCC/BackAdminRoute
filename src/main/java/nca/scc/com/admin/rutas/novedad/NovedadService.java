package nca.scc.com.admin.rutas.novedad;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.novedad.entity.Novedad;
import nca.scc.com.admin.rutas.novedad.entity.enums.EstadoAprobacion;
import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.sede.SedeRepository;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NovedadService {

    private final NovedadRepository repository;
    private final RutaRepository rutaRepository;
    private final SedeRepository sedeRepository;

    public NovedadService(NovedadRepository repository, RutaRepository rutaRepository, SedeRepository sedeRepository) {
        this.repository = repository;
        this.rutaRepository = rutaRepository;
        this.sedeRepository = sedeRepository;
    }

    public Novedad create(Novedad novedad) {
        return repository.save(novedad);
    }

    public List<Novedad> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role != null && role == Role.ROLE_TRANSPORT && tenant != null) {
            List<Sede> sedes = sedeRepository.findByTransportId(tenant);
            var sedeIds = sedes.stream().map(Sede::getId).toList();
            return repository.findAll().stream().filter(n -> {
                return rutaRepository.findById(n.getRutaId()).map(r -> r.sedeId() != null && sedeIds.contains(r.sedeId())).orElse(false);
            }).collect(Collectors.toList());
        } else if (role != null && role == Role.ROLE_SCHOOL && tenant != null) {
            return repository.findAll().stream().filter(n -> {
                return rutaRepository.findById(n.getRutaId()).map(r -> tenant.equals(r.sedeId())).orElse(false);
            }).collect(Collectors.toList());
        } else {
            return repository.findAll();
        }
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
