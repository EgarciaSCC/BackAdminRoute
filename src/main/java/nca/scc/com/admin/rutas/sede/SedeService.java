package nca.scc.com.admin.rutas.sede;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.TenantContext;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.rutas.colegio.ColegioService;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SedeService {

    private static final Logger log = LoggerFactory.getLogger(SedeService.class);
    private final SedeRepository repository;
    private final ColegioService colegioService;

    public SedeService(SedeRepository repository, ColegioService colegioService) {
        this.repository = repository;
        this.colegioService = colegioService;
    }

    public Sede create(Sede sede) {
        log.debug("Creando sede: {}", sede.getNombre());

        String tenant = TenantContext.getCurrentTenant();

        // Validar que colegioId existe y pertenece al mismo tenant
        colegioService.getById(sede.getColegioId());

        sede.setTenant(tenant);
        Sede saved = repository.save(sede);
        log.info("Sede creada: {} (ID: {}, Colegio: {}, Tenant: {})", sede.getNombre(), saved.getId(), sede.getColegioId(), tenant);
        return saved;
    }

    public List<Sede> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role == Role.ROLE_TRANSPORT && tenant != null) {
            return repository.findByTransportId(tenant);
        } else if (role == Role.ROLE_SCHOOL && tenant != null) {
            return repository.findAll().stream().filter(s -> s.getId().equals(tenant)).toList();
        } else {
            return repository.findAll();
        }
    }

    public Sede getById(String id) {
        String tenant = TenantContext.getCurrentTenant();
        Sede sede = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada: " + id));

        if (sede.getTenant() != null && !tenant.contains(sede.getTenant())) {
            log.warn("Acceso denegado a sede {} para tenant {}", id, tenant);
            throw new NotFoundException("Sede no encontrada");
        }

        return sede;
    }

    public Sede update(String id, Sede sede) {
        log.debug("Actualizando sede: {}", id);
        String tenant = TenantContext.getCurrentTenant();

        Sede existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada: " + id));

        if (existing.getTenant() != null && !existing.getTenant().equals(tenant)) {
            log.warn("Acceso denegado al actualizar sede {} para tenant {}", id, tenant);
            throw new NotFoundException("Sede no encontrada");
        }

        // Validar que colegioId existe si cambió
        if (sede.getColegioId() != null && !sede.getColegioId().equals(existing.getColegioId())) {
            colegioService.getById(sede.getColegioId());
            existing.setColegioId(sede.getColegioId());
        }

        existing.setNombre(sede.getNombre());
        existing.direccion(sede.direccion());
        existing.ciudad(sede.ciudad());
        existing.lat(sede.lat());
        existing.lng(sede.lng());

        Sede updated = repository.save(existing);
        log.info("Sede actualizada: {}", id);
        return updated;
    }

    public void delete(String id) {
        log.debug("Eliminando sede: {}", id);
        String tenant = TenantContext.getCurrentTenant();

        Sede sede = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada: " + id));

        if (sede.getTenant() != null && !sede.getTenant().equals(tenant)) {
            log.warn("Acceso denegado al eliminar sede {} para tenant {}", id, tenant);
            throw new NotFoundException("Sede no encontrada");
        }

        repository.deleteById(id);
        log.info("Sede eliminada: {}", id);
    }
}
