package nca.scc.com.admin.rutas.coordinador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import nca.scc.com.admin.rutas.TenantContext;

import java.util.List;

/**
 * Servicio para gestión de Coordinadores
 * 
 * Responsabilidades:
 * - CRUD completo con validaciones
 * - Filtrado automático por tenant
 * - Validación de unicidad de cédula
 */
@Service
public class CoordinadorService {

    private static final Logger log = LoggerFactory.getLogger(CoordinadorService.class);
    private final CoordinadorRepository repository;

    public CoordinadorService(CoordinadorRepository repository) {
        this.repository = repository;
    }

    public Coordinador create(Coordinador coordinador) {
        log.debug("Creando coordinador: {}", coordinador.getNombre());

        String tenant = TenantContext.getCurrentTenant();

        // Validar cédula única
        repository.findByCedula(coordinador.getCedula()).ifPresent(c -> {
            throw new IllegalArgumentException("Ya existe coordinador con cédula: " + coordinador.getCedula());
        });

        coordinador.setTenant(tenant);
        Coordinador saved = repository.save(coordinador);
        log.info("Coordinador creado: {} (ID: {}, Tenant: {})", coordinador.getNombre(), saved.getId(), tenant);
        return saved;
    }

    public List<Coordinador> listAll() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando coordinadores para tenant: {}", tenant);
        return repository.findByTenant(tenant);
    }

    public Coordinador getById(String id) {
        String tenant = TenantContext.getCurrentTenant();
        Coordinador coordinador = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coordinador no encontrado: " + id));

        if (!coordinador.getTenant().equals(tenant)) {
            log.warn("Acceso denegado al coordinador {} para tenant {}", id, tenant);
            throw new NotFoundException("Coordinador no encontrado");
        }
        return coordinador;
    }

    public Coordinador update(String id, Coordinador coordinador) {
        log.debug("Actualizando coordinador: {}", id);

        Coordinador existing = getById(id);

        // Validar cambio de cédula
        if (coordinador.getCedula() != null && !coordinador.getCedula().equals(existing.getCedula())) {
            repository.findByCedula(coordinador.getCedula()).ifPresent(c -> {
                throw new IllegalArgumentException("Ya existe coordinador con cédula: " + coordinador.getCedula());
            });
        }

        existing.setNombre(coordinador.getNombre());
        existing.setCedula(coordinador.getCedula());
        existing.setTelefono(coordinador.getTelefono());
        existing.setEmail(coordinador.getEmail());
        if (coordinador.getEstado() != null) {
            existing.setEstado(coordinador.getEstado());
        }
        if (coordinador.getActivo() != null) {
            existing.setActivo(coordinador.getActivo());
        }

        Coordinador updated = repository.save(existing);
        log.info("Coordinador actualizado: {}", id);
        return updated;
    }

    public void delete(String id) {
        log.debug("Eliminando coordinador: {}", id);
        Coordinador coordinador = getById(id);
        coordinador.setActivo(false);
        repository.save(coordinador);
        log.info("Coordinador eliminado (soft delete): {}", id);
    }

    public List<Coordinador> listActivos() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando coordinadores activos para tenant: {}", tenant);
        return repository.findActivosByTenant(tenant);
    }
}
