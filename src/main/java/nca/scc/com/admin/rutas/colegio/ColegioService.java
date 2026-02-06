package nca.scc.com.admin.rutas.colegio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import nca.scc.com.admin.rutas.TenantContext;
import nca.scc.com.admin.rutas.colegio.entity.Colegio;
import nca.scc.com.admin.rutas.NotFoundException;

import java.util.List;

/**
 * Servicio para gestión de Colegios
 * Responsabilidades:
 * - CRUD completo con validaciones
 * - Filtrado automático por tenant
 * - Validación de unicidad (NIT)
 */
@Service
public class ColegioService {

    private static final Logger log = LoggerFactory.getLogger(ColegioService.class);
    private final ColegioRepository repository;

    public ColegioService(ColegioRepository repository) {
        this.repository = repository;
    }

    /**
     * Crear nuevo colegio
     */
    public Colegio create(Colegio colegio) {
        log.debug("Creando colegio: {}", colegio.getNombre());

        String tenant = TenantContext.getCurrentTenant();

        // Validar NIT único globalmente
        repository.findByNit(colegio.getNit()).ifPresent(c -> {
            throw new IllegalArgumentException("Ya existe colegio con NIT: " + colegio.getNit());
        });

        colegio.setTenant(tenant);

        Colegio saved = repository.save(colegio);
        log.info("Colegio creado: {} (ID: {}, Tenant: {})", colegio.getNombre(), saved.getId(), tenant);
        return saved;
    }

    /**
     * Listar colegios del tenant actual
     */
    public List<Colegio> listAll() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando colegios para tenant: {}", tenant);
        return repository.findByTenant(tenant);
    }

    /**
     * Obtener colegio por ID (con validación de tenant)
     */
    public Colegio getById(String id) {
        String tenant = TenantContext.getCurrentTenant();
        Colegio colegio = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Colegio no encontrado: " + id));

        if (!colegio.getTenant().equals(tenant)) {
            log.warn("Acceso denegado al colegio {} para tenant {}", id, tenant);
            throw new NotFoundException("Colegio no encontrado");
        }

        return colegio;
    }

    /**
     * Actualizar colegio
     */
    public Colegio update(String id, Colegio colegio) {
        log.debug("Actualizando colegio: {}", id);
        String tenant = TenantContext.getCurrentTenant();

        Colegio existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Colegio no encontrado: " + id));

        if (!existing.getTenant().equals(tenant)) {
            log.warn("Acceso denegado al actualizar colegio {} para tenant {}", id, tenant);
            throw new NotFoundException("Colegio no encontrado");
        }

        // Validar NIT único (si cambió)
        if (!existing.getNit().equals(colegio.getNit())) {
            repository.findByNit(colegio.getNit()).ifPresent(c -> {
                throw new IllegalArgumentException("Ya existe colegio con NIT: " + colegio.getNit());
            });
            existing.setNit(colegio.getNit());
        }

        existing.setNombre(colegio.getNombre());
        existing.setDireccion(colegio.getDireccion());
        existing.setCiudad(colegio.getCiudad());
        existing.setContacto(colegio.getContacto());
        existing.setEmail(colegio.getEmail());
        existing.setLogoUrl(colegio.getLogoUrl());
        if (colegio.getActivo() != null) {
            existing.setActivo(colegio.getActivo());
        }

        Colegio updated = repository.save(existing);
        log.info("Colegio actualizado: {}", id);
        return updated;
    }

    /**
     * Eliminar colegio (soft delete)
     */
    public void delete(String id) {
        log.debug("Eliminando colegio: {}", id);
        String tenant = TenantContext.getCurrentTenant();

        Colegio colegio = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Colegio no encontrado: " + id));

        if (!colegio.getTenant().equals(tenant)) {
            log.warn("Acceso denegado al eliminar colegio {} para tenant {}", id, tenant);
            throw new NotFoundException("Colegio no encontrado");
        }

        colegio.setActivo(false);
        repository.save(colegio);
        log.info("Colegio eliminado (soft): {}", id);
    }

    /**
     * Listar colegios activos del tenant actual
     */
    public List<Colegio> listActivos() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando colegios activos para tenant: {}", tenant);
        return repository.findActivosByTenant(tenant);
    }
}
