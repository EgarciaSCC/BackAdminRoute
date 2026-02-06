package nca.scc.com.admin.rutas.conductor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import nca.scc.com.admin.rutas.conductor.entity.enums.ConductorState;
import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.TenantContext;

import java.util.List;

/**
 * Servicio para gestión de Conductores
 *
 * Responsabilidades:
 * - CRUD completo con validaciones
 * - Filtrado automático por tenant
 * - Validación de unicidad (cédula, licencia)
 * - Cambio de estado
 */
@Service
public class ConductorService {

    private static final Logger log = LoggerFactory.getLogger(ConductorService.class);
    private final ConductorRepository repository;

    public ConductorService(ConductorRepository repository) {
        this.repository = repository;
    }

    /**
     * Crear nuevo conductor
     */
    public Conductor create(Conductor conductor) {
        log.debug("Creando conductor: {}", conductor.getNombre());

        String tenant = TenantContext.getCurrentTenant();

        // Validar cédula única globalmente
        repository.findByCedula(conductor.getCedula()).ifPresent(c -> {
            throw new IllegalArgumentException("Ya existe conductor con cédula: " + conductor.getCedula());
        });

        // Validar licencia única globalmente
        repository.findByLicencia(conductor.getLicencia()).ifPresent(c -> {
            throw new IllegalArgumentException("Ya existe conductor con licencia: " + conductor.getLicencia());
        });

        conductor.setTenant(tenant);
        if (conductor.getEstado() == null) {
            conductor.setEstado(ConductorState.disponible);
        }

        Conductor saved = repository.save(conductor);
        log.info("Conductor creado: {} (ID: {}, Tenant: {})", conductor.getNombre(), saved.getId(), tenant);
        return saved;
    }

    /**
     * Listar conductores del tenant actual
     */
    public List<Conductor> listAll() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando conductores para tenant: {}", tenant);
        return repository.findByTenant(tenant);
    }

    /**
     * Obtener conductor por ID (con validación de tenant)
     */
    public Conductor getById(String id) {
        String tenant = TenantContext.getCurrentTenant();
        Conductor conductor = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conductor no encontrado: " + id));

        if (!conductor.getTenant().equals(tenant)) {
            log.warn("Acceso denegado al conductor {} para tenant {}", id, tenant);
            throw new NotFoundException("Conductor no encontrado");
        }

        return conductor;
    }

    /**
     * Actualizar conductor
     */
    public Conductor update(String id, Conductor conductor) {
        log.debug("Actualizando conductor: {}", id);

        Conductor existing = getById(id);

        // Validar cambio de cédula
        if (conductor.getCedula() != null && !conductor.getCedula().equals(existing.getCedula())) {
            repository.findByCedula(conductor.getCedula()).ifPresent(c -> {
                throw new IllegalArgumentException("Ya existe conductor con cédula: " + conductor.getCedula());
            });
        }

        // Validar cambio de licencia
        if (conductor.getLicencia() != null && !conductor.getLicencia().equals(existing.getLicencia())) {
            repository.findByLicencia(conductor.getLicencia()).ifPresent(c -> {
                throw new IllegalArgumentException("Ya existe conductor con licencia: " + conductor.getLicencia());
            });
        }

        existing.setNombre(conductor.getNombre());
        existing.setCedula(conductor.getCedula());
        existing.setTelefono(conductor.getTelefono());
        existing.setLicencia(conductor.getLicencia());
        existing.setTipoLicencia(conductor.getTipoLicencia());
        if (conductor.getEstado() != null) {
            existing.setEstado(conductor.getEstado());
        }
        if (conductor.getActivo() != null) {
            existing.setActivo(conductor.getActivo());
        }

        Conductor updated = repository.save(existing);
        log.info("Conductor actualizado: {}", id);
        return updated;
    }

    /**
     * Eliminar conductor (soft delete)
     */
    public void delete(String id) {
        log.debug("Eliminando conductor: {}", id);
        Conductor conductor = getById(id);
        conductor.setActivo(false);
        repository.save(conductor);
        log.info("Conductor eliminado (soft delete): {}", id);
    }

    /**
     * Cambiar estado del conductor
     */
    public Conductor changeState(String id, ConductorState nuevoEstado) {
        log.debug("Cambiando estado de conductor {} a {}", id, nuevoEstado);
        Conductor conductor = getById(id);
        conductor.setEstado(nuevoEstado);
        Conductor updated = repository.save(conductor);
        log.info("Estado de conductor {} cambió a: {}", id, nuevoEstado);
        return updated;
    }

    /**
     * Listar conductores disponibles del tenant actual
     */
    public List<Conductor> listDisponibles() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando conductores disponibles para tenant: {}", tenant);
        return repository.findByTenantAndEstado(tenant, ConductorState.disponible);
    }

    /**
     * Listar conductores activos del tenant actual
     */
    public List<Conductor> listActivos() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando conductores activos para tenant: {}", tenant);
        return repository.findActivosByTenant(tenant);
    }
}
