package nca.scc.com.admin.rutas.pasajero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.TenantContext;

import java.util.List;

/**
 * Servicio para gestión de Pasajeros/Estudiantes
 * Responsabilidades:
 * - CRUD completo con validaciones
 * - Filtrado automático por tenant
 * - Validación de matrícula única
 */
@Service
public class PasajeroService {

    private static final Logger log = LoggerFactory.getLogger(PasajeroService.class);
    private final PasajeroRepository repository;

    public PasajeroService(PasajeroRepository repository) {
        this.repository = repository;
    }

    /**
     * Crear nuevo pasajero/estudiante
     */
    public Pasajero create(Pasajero pasajero) {
        log.debug("Creando pasajero: {}", pasajero.getNombre());

        String tenant = TenantContext.getCurrentTenant();
        log.debug("Tenant : {}", tenant);
        // Validar matrícula única
        repository.findByMatricula(pasajero.getMatricula()).ifPresent(p -> {
            throw new IllegalArgumentException("Ya existe estudiante con matrícula: " + pasajero.getMatricula());
        });

        pasajero.setTenant(tenant);
        Pasajero saved = repository.save(pasajero);
        log.info("Pasajero creado: {} (ID: {}, Tenant: {})", pasajero.getNombre(), saved.getId(), tenant);
        return saved;
    }

    /**
     * Listar pasajeros del tenant actual
     */
    public List<Pasajero> listAll() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando pasajeros para tenant: {}", tenant);
        return repository.findByTenant(tenant);
    }

    /**
     * Obtener pasajero por ID (con validación de tenant)
     */
    public Pasajero getById(String id) {
        String tenant = TenantContext.getCurrentTenant();
        Pasajero pasajero = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pasajero no encontrado: " + id));

        if (!pasajero.getTenant().equals(tenant)) {
            log.warn("Acceso denegado al pasajero {} para tenant {}", id, tenant);
            throw new NotFoundException("Pasajero no encontrado");
        }
        return pasajero;
    }

    /**
     * Listar pasajeros por sede
     */
    public List<Pasajero> listBySedeId(String sedeId) {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando pasajeros para sede: {} (tenant: {})", sedeId, tenant);
        return repository.findBySedeIdAndTenant(sedeId, tenant);
    }

    /**
     * Actualizar pasajero
     */
    public Pasajero update(String id, Pasajero pasajero) {
        log.debug("Actualizando pasajero: {}", id);

        Pasajero existing = getById(id);

        // Validar cambio de matrícula
        if (pasajero.getMatricula() != null && !pasajero.getMatricula().equals(existing.getMatricula())) {
            repository.findByMatricula(pasajero.getMatricula()).ifPresent(p -> {
                throw new IllegalArgumentException("Ya existe estudiante con matrícula: " + pasajero.getMatricula());
            });
        }

        existing.setNombre(pasajero.getNombre());
        existing.setMatricula(pasajero.getMatricula());
        existing.setCurso(pasajero.getCurso());
        existing.setDireccion(pasajero.getDireccion());
        existing.setBarrio(pasajero.getBarrio());
        existing.setLat(pasajero.getLat());
        existing.setLng(pasajero.getLng());
        existing.setSedeId(pasajero.getSedeId());
        existing.setPadreId(pasajero.getPadreId());
        existing.setTelefonoEmergencia(pasajero.getTelefonoEmergencia());
        existing.setAlergias(pasajero.getAlergias());
        existing.setNotas(pasajero.getNotas());
        if (pasajero.getActivo() != null) {
            existing.setActivo(pasajero.getActivo());
        }

        Pasajero updated = repository.save(existing);
        log.info("Pasajero actualizado: {}", id);
        return updated;
    }

    /**
     * Eliminar pasajero (soft delete)
     */
    public void delete(String id) {
        log.debug("Eliminando pasajero: {}", id);
        Pasajero pasajero = getById(id);
        pasajero.setActivo(false);
        repository.save(pasajero);
        log.info("Pasajero eliminado (soft delete): {}", id);
    }

    /**
     * Listar pasajeros activos
     */
    public List<Pasajero> listActivos() {
        String tenant = TenantContext.getCurrentTenant();
        log.debug("Listando pasajeros activos para tenant: {}", tenant);
        return repository.findActivosByTenant(tenant);
    }
}
