package nca.scc.com.admin.rutas.coordinador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import nca.scc.com.admin.rutas.coordinador.dto.CreateCoordinadorWithUserRequest;
import nca.scc.com.admin.rutas.TenantContext;
import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.auth.Role;
import nca.scc.com.admin.security.SecurityUtils;

import java.util.List;

/**
 * Servicio para gestión de Coordinadores
 * 
 * Responsabilidades:
 * - CRUD completo con validaciones
 * - Filtrado automático por tenant
 * - Validación de unicidad de cédula
 * - Creación de coordinador con usuario asociado en un paso
 */
@Service
public class CoordinadorService {

    private static final Logger log = LoggerFactory.getLogger(CoordinadorService.class);
    private final CoordinadorRepository repository;
    private final UsuarioRepository usuarioRepository;

    public CoordinadorService(CoordinadorRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
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

    /**
     * Crear Coordinador CON USUARIO ASOCIADO en un único paso
     * Este método simplifica el onboarding de coordinadores
     */
    public Coordinador createCoordinadorWithUser(CreateCoordinadorWithUserRequest request) {
        log.debug("Creando coordinador con usuario: {}", request.getNombre());

        String tenant = request.getTenant() != null ? request.getTenant() : TenantContext.getCurrentTenant();

        // 1. Validar duplicados
        repository.findByCedula(request.getCedula()).ifPresent(c -> {
            throw new IllegalArgumentException("Ya existe coordinador con cédula: " + request.getCedula());
        });

        usuarioRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Ya existe usuario con username: " + request.getUsername());
        });

        // 2. Crear Coordinador
        Coordinador coordinador = new Coordinador();
        coordinador.setNombre(request.getNombre());
        coordinador.setCedula(request.getCedula());
        coordinador.setEmail(request.getEmail());
        coordinador.setTelefono(request.getTelefono());
        coordinador.setEstado(request.getEstado());
        coordinador.setTenant(tenant);

        Coordinador savedCoordinador = repository.save(coordinador);
        log.info("✅ Coordinador creado: {} (ID: {})", coordinador.getNombre(), savedCoordinador.getId());

        // 3. Crear Usuario vinculado al Coordinador
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        Usuario usuario = new Usuario(
            request.getNombre(),
            request.getUsername(),
            hashedPassword,
            tenant,
            Role.ROLE_TRANSPORT
        );
        usuario.setEmail(request.getEmail());
        usuario.setCoordinadorId(savedCoordinador.getId());  // ✅ VINCULAR AL COORDINADOR

        usuarioRepository.save(usuario);
        log.info("✅ Usuario creado: {} - vinculado a coordinador: {}", request.getUsername(), savedCoordinador.getId());
        log.info("   📱 Username: {} | Contraseña: {} (hash guardado)", request.getUsername(), request.getPassword());

        return savedCoordinador;
    }

    public List<Coordinador> listAll() {
        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role == Role.ROLE_ADMIN) {
            return repository.findAll();
        }
        if (tenant == null) return List.of();
        return repository.findByTenant(tenant);
    }

    public Coordinador getById(String id) {
        Coordinador coordinador = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coordinador no encontrado: " + id));

        Role role = SecurityUtils.getRoleClaim();
        String tenant = SecurityUtils.getTenantClaim("tid");

        if (role == Role.ROLE_ADMIN) return coordinador;
        if (tenant != null && coordinador.getTenant().equals(tenant)) return coordinador;

        log.warn("Acceso denegado al coordinador {} para tenant {} (rol={})", id, tenant, role);
        throw new NotFoundException("Coordinador no encontrado");
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
