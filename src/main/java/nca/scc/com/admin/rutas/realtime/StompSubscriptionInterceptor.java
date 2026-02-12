package nca.scc.com.admin.rutas.realtime;

import nca.scc.com.admin.rutas.auth.UsuarioRepository;
import nca.scc.com.admin.rutas.auth.entity.Usuario;
import nca.scc.com.admin.rutas.ruta.RutaRepository;
import nca.scc.com.admin.rutas.ruta.entity.Ruta;
import nca.scc.com.admin.rutas.rutaPasajeros.RutaPasajeroRepository;
import nca.scc.com.admin.rutas.rutaPasajeros.entity.RutaPasajero;
import nca.scc.com.admin.rutas.sede.SedeRepository;
import nca.scc.com.admin.rutas.sede.entity.Sede;
import nca.scc.com.admin.rutas.pasajero.PasajeroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StompSubscriptionInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(StompSubscriptionInterceptor.class);

    private final UsuarioRepository usuarioRepository;
    private final RutaRepository rutaRepository;
    private final SedeRepository sedeRepository;
    private final PasajeroRepository pasajeroRepository;
    private final RutaPasajeroRepository rutaPasajeroRepository;

    public StompSubscriptionInterceptor(UsuarioRepository usuarioRepository, RutaRepository rutaRepository, SedeRepository sedeRepository, PasajeroRepository pasajeroRepository, RutaPasajeroRepository rutaPasajeroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rutaRepository = rutaRepository;
        this.sedeRepository = sedeRepository;
        this.pasajeroRepository = pasajeroRepository;
        this.rutaPasajeroRepository = rutaPasajeroRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String dest = accessor.getDestination();
            if (dest != null && dest.startsWith("/topic/positions/")) {
                String rutaId = dest.substring("/topic/positions/".length());
                String userId = accessor.getUser() != null ? accessor.getUser().getName() : null;
                if (userId == null) {
                    log.warn("Subscription denied: anonymous user to {}", dest);
                    return null; // deny
                }
                Optional<Usuario> userOpt = usuarioRepository.findById(userId);
                if (userOpt.isEmpty()) {
                    log.warn("Subscription denied: user not found {}", userId);
                    return null;
                }
                Usuario user = userOpt.get();

                Optional<Ruta> rutaOpt = rutaRepository.findById(rutaId);
                if (rutaOpt.isEmpty()) {
                    log.warn("Subscription denied: ruta not found {}", rutaId);
                    return null;
                }
                Ruta ruta = rutaOpt.get();

                // Admin can subscribe to any
                if (user.getRole() != null && user.getRole().name().equals("ROLE_ADMIN")) {
                    return message;
                }

                String userTenant = user.getTenant();

                // Transport role: userTenant is transportId; check if route's sede belongs to that transport
                if (user.getRole() != null && user.getRole().name().equals("ROLE_TRANSPORT")) {
                    if (ruta.getSedeId() != null) {
                        Sede sede = sedeRepository.findById(ruta.getSedeId()).orElse(null);
                        if (sede != null && userTenant != null && userTenant.equals(sede.getTransportId())) {
                            return message;
                        }
                    }
                    log.warn("Subscription denied: transport user {} not allowed to subscribe to ruta {}", userId, rutaId);
                    return null;
                }

                // Parent role (ROLE_SCHOOL maps to frontend "parent"): allow only if has child in route
                // Parent is a user with ROLE_SCHOOL where tenant is a Padre (in sistema, padre_id in pasajero table)
                // Check if user (as padre) has at least one child assigned to this route
                if (user.getRole() != null && user.getRole().name().equals("ROLE_SCHOOL")) {
                    // Check if this ROLE_SCHOOL user is actually a parent (has padreId references)
                    List<RutaPasajero> rutaPasajeros = rutaPasajeroRepository.findByIdRuta(rutaId);

                    if (rutaPasajeros != null && !rutaPasajeros.isEmpty()) {
                        //buscar padre para algun estudiante de la ruta
                        List<String> estudianteIds = rutaPasajeros.stream()
                                .map(RutaPasajero::getId).toList()
                                .stream().map(rutaPasajeroId -> String.valueOf(rutaPasajeroId.getRutaId().equals(rutaId))).toList();

                        boolean exists = pasajeroRepository.existsAnyByIdsAndPadreId(estudianteIds, userId);
                        if (exists) {
                            return message; // authorized
                        }
                    }
                    log.warn("Subscription denied: parent user {} not associated to any estudiante in ruta {}", userId, rutaId);
                    return null;
                }

                // Default deny
                log.warn("Subscription denied: user {} with role {} not authorized for ruta {}", userId, user.getRole(), rutaId);
                return null;
            }
        }
        return message;
    }
}
