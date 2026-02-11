package nca.scc.com.admin.rutas.coordinador;

import nca.scc.com.admin.rutas.NotFoundException;
import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.auth.Role;
import nca.scc.com.admin.rutas.coordinador.entity.Coordinador;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para funcionalidades de Coordinador autenticado.
 * Similar a DriverService pero para coordinadores.
 *
 * Responsabilidades:
 * - Resolver coordinador a partir del token JWT
 * - Retornar rutas asignadas para hoy
 * - Retornar historial de rutas completadas
 */
@Service
public class CoordinatorService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final UsuarioRepository usuarioRepository;
    private final CoordinadorRepository coordinadorRepository;

    public CoordinatorService(UsuarioRepository usuarioRepository,
                             CoordinadorRepository coordinadorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.coordinadorRepository = coordinadorRepository;
    }

    /**
     * Resuelve el coordinador asociado al usuario autenticado.
     * Requiere rol ROLE_TRANSPORT y coordinadorId en Usuario.
     *
     * @return Coordinador autenticado
     * @throws ResponseStatusException si no está autenticado o no tiene coordinadorId
     */
    public Coordinador resolveCoordinatorFromAuth() {
        Jwt jwt = getJwt();
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT requerido");
        }
        String username = jwt.getSubject();
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no encontrado"));
        if (user.getRole() != Role.ROLE_TRANSPORT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol ROLE_TRANSPORT requerido");
        }
        String coordinadorId = user.getCoordinadorId();
        if (coordinadorId == null || coordinadorId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coordinador no asociado al usuario");
        }
        return coordinadorRepository.findById(coordinadorId)
                .orElseThrow(() -> new NotFoundException("Coordinador no encontrado: " + coordinadorId));
    }

    /**
     * Obtiene el JWT del contexto de seguridad
     */
    private Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
            return null;
        }
        return (Jwt) auth.getPrincipal();
    }

    /**
     * Parsea una hora en formato HH:mm
     */
    protected java.time.LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return null;
        try {
            return java.time.LocalTime.parse(timeStr, TIME_FMT);
        } catch (Exception e) {
            return null;
        }
    }
}
