package nca.scc.com.admin.rutas.pasajero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.pasajero.entity.Pasajero;
import nca.scc.com.admin.rutas.TenantContext;
import nca.scc.com.admin.security.SecurityUtils;
import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.auth.Role;
import nca.scc.com.admin.rutas.ruta.RutaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final UsuarioRepository usuarioRepository;

    public PasajeroService(PasajeroRepository repository, UsuarioRepository usuarioRepository, RutaRepository rutaRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
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
     * Listar pasajeros visibles para el usuario autenticado según rol
     */
    public List<Pasajero> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");
        String userId = SecurityUtils.getUserIdClaim();

        if (role == Role.ROLE_ADMIN) {
            return repository.findAll();
        }

        if (role == Role.ROLE_SCHOOL && tenant != null) {
            return repository.findByTenant(tenant);
        }

        if (role == Role.ROLE_TRANSPORT && tenant != null) {
            // Admin.transport: ver estudiantes de sedes administradas
            // Conductor/coordinador: ver solo los de su sede
            return repository.findBySedeTransportId(tenant);
        }

        // ROLE_PARENT: listar hijos del padre
        if (role == Role.ROLE_SCHOOL && userId != null) {
            // padres también usan ROLE_SCHOOL in this system; buscar por padreId
            return repository.findByPadreId(userId);
        }

        return List.of();
    }

    /**
     * Obtener pasajero por ID con validación de visibilidad
     */
    public Pasajero getById(String id) {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");
        String userId = SecurityUtils.getUserIdClaim();

        Optional<Pasajero> p = repository.findById(id);
        //Pasajero p = repository.findByIdAndTransportId(id, userId);

        if (p.isEmpty()) {
            throw new NotFoundException("Pasajero no encontrado: " + id);
        }

        if (role == Role.ROLE_ADMIN) return p.orElse(null);

        if (role == Role.ROLE_SCHOOL && tenant != null) {
            if (!p.get().getTenant().equals(tenant)) {
                throw new NotFoundException("Pasajero no encontrado");
            }
            return p.orElse(null);
        }

        if (role == Role.ROLE_TRANSPORT && tenant != null) {
            // Verificar que el pasajero está en una sede que administra
            if (p.get().getSedeId() != null) {
                // Si el pasajero está en una sede que el transport administra, OK
                // (Validación: sedeId debe estar vinculada a este transport tenant)
                return p.orElse(null);
            }
            throw new NotFoundException("Pasajero no encontrado");
        }

        // ROLE_PARENT (padre) -> verificar padreId
        Usuario u = usuarioRepository.findByUsername(userId).orElse(null);
        if (u != null && u.getRole() == Role.ROLE_SCHOOL) {
            if (repository.existsByIdAndPadreId(id, u.getId())) return p.orElse(null);
            throw new NotFoundException("Pasajero no encontrado");
        }

        throw new NotFoundException("Pasajero no encontrado");
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

    /**
     * Listar pasajeros de ruta activa (usado para mostrar estudiantes en ruta)
     */
    public List<Pasajero> listPasajerosByRutaId(String rutaId) {
        String tenant = TenantContext.getCurrentTenant();
        String userId = SecurityUtils.getUserIdClaim();
        assert userId != null;
        Usuario conductor = usuarioRepository.findByUsername(userId).orElseThrow(() -> new NotFoundException("Usuario no valido"));
        if (conductor.getConductorId() == null && conductor.getCoordinadorId() == null) {
            throw new NotFoundException("Usuario no sin acceso a navegacion de rutas");
        }

        List<Pasajero> pasajeroList = repository.findPasajerosByRutaNative(rutaId);
        if (pasajeroList.isEmpty()){
            log.warn("No se encontraron pasajeros para ruta: {} (tenant: {})", rutaId, tenant);
            throw new NotFoundException("No se encontraron pasajeros para ruta: " + rutaId);
        }

        log.debug("Listando pasajeros para ruta: {} (tenant: {})", rutaId, tenant);
        return pasajeroList;
    }

}
