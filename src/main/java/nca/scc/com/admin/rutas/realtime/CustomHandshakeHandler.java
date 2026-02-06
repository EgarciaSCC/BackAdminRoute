package nca.scc.com.admin.rutas.realtime;

import nca.scc.com.admin.rutas.auth.entity.Usuario;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

@Override
protected Principal determineUser(ServerHttpRequest request, org.springframework.web.socket.WebSocketHandler wsHandler, Map<String, Object> attributes) {
    Object u = attributes.get("user");
    if (u instanceof Usuario) {
        Usuario user = (Usuario) u;
        return () -> user.getId();
    }
    // fallback to random uuid principal to avoid null
    String id = UUID.randomUUID().toString();
    return () -> id;
}
}
