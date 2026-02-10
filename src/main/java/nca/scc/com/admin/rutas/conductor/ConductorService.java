package nca.scc.com.admin.rutas.conductor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

import nca.scc.com.admin.rutas.conductor.entity.Conductor;
import nca.scc.com.admin.rutas.conductor.entity.enums.ConductorState;
import nca.scc.com.admin.rutas.conductor.dto.CreateConductorWithUserRequest;
import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.TenantContext;
import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.auth.Role;
import nca.scc.com.admin.security.SecurityUtils;

import java.util.List;

/**
 * Servicio para gestión de Conductores
 *
 * Responsabilidades:
 * - CRUD completo con validaciones
 * - Filtrado automático por tenant
 * - Validación de unicidad (cédula, licencia)
 * - Cambio de estado
 * - Creación de conductor con usuario asociado en un paso
 */
@Service
public class ConductorService {

    private static final Logger log = LoggerFactory.getLogger(ConductorService.class);
    private final ConductorRepository repository;
    private final UsuarioRepository usuarioRepository;

    public ConductorService(ConductorRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
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
     * Crear Conductor CON USUARIO ASOCIADO en un único paso
     * Este método simplifica el onboarding de conductores
     */
    public Conductor createConductorWithUser(CreateConductorWithUserRequest request) {
        log.debug("Creando conductor con usuario: {}", request.getNombre());

        String tenant = request.getTenant() != null ? request.getTenant() : TenantContext.getCurrentTenant();

        // 1. Validar duplicados
        repository.findByCedula(request.getCedula()).ifPresent(c -> {
            throw new IllegalArgumentException("Ya existe conductor con cédula: " + request.getCedula());
        });

        repository.findByLicencia(request.getLicencia()).ifPresent(c -> {
            throw new IllegalArgumentException("Ya existe conductor con licencia: " + request.getLicencia());
        });

        usuarioRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Ya existe usuario con username: " + request.getUsername());
        });

        // 2. Crear Conductor
        Conductor conductor = new Conductor();
        conductor.setNombre(request.getNombre());
        conductor.setCedula(request.getCedula());
        conductor.setLicencia(request.getLicencia());
        conductor.setTipoLicencia(request.getTipoLicencia());
        conductor.setEstado(request.getEstado() != null ? request.getEstado() : ConductorState.disponible);
        conductor.setTenant(tenant);
        conductor.setTelefono(request.getTelefonoEmergencia());  // Usar telefono del conductor

        Conductor savedConductor = repository.save(conductor);
        log.info("✅ Conductor creado: {} (ID: {})", conductor.getNombre(), savedConductor.getId());

        // 3. Crear Usuario vinculado al Conductor
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        Usuario usuario = new Usuario(
            request.getNombre(),
            request.getUsername(),
            hashedPassword,
            tenant,
            Role.ROLE_TRANSPORT
        );
        usuario.setEmail(request.getEmail());
        usuario.setConductorId(savedConductor.getId());  // ✅ VINCULAR AL CONDUCTOR

        usuarioRepository.save(usuario);
        log.info("✅ Usuario creado: {} - vinculado a conductor: {}", request.getUsername(), savedConductor.getId());
        log.info("   📱 Username: {} | Contraseña: {} (hash guardado)", request.getUsername(), request.getPassword());

        return savedConductor;
    }

    /**
     * Listar conductores del tenant actual
     */
    public List<Conductor> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role == Role.ROLE_ADMIN) {
            return repository.findAll();
        }
        if (tenant == null) return List.of();

        // ROLE_TRANSPORT / ROLE_SCHOOL: ver conductores de su tenant
        return repository.findByTenant(tenant);
    }

    /**
     * Obtener conductor por ID (con validación de tenant)
     */
    public Conductor getById(String id) {
        Conductor conductor = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Conductor no encontrado: " + id));

        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role == Role.ROLE_ADMIN) return conductor;

        if (tenant != null && conductor.getTenant().equals(tenant)) return conductor;

        log.warn("Acceso denegado al conductor {} para tenant {} (rol={})", id, tenant, role);
        throw new NotFoundException("Conductor no encontrado");
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
