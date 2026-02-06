package nca.scc.com.admin.rutas.realtime;

import nca.scc.com.admin.rutas.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Enumeration;

public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthHandshakeInterceptor.class);

    private final AuthService authService;

    public WebSocketAuthHandshakeInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = null;
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest ssr = (ServletServerHttpRequest) request;
            Enumeration<String> headers = ssr.getServletRequest().getHeaders("Authorization");
            List<String> auth = headers == null ? List.of() : Collections.list(headers);
            if (auth != null && !auth.isEmpty()) {
                String v = auth.get(0);
                if (v != null && v.startsWith("Bearer ")) token = v.substring(7).trim();
            }
            if (token == null || token.isBlank()) {
                String qs = ssr.getServletRequest().getQueryString();
                if (qs != null && qs.contains("token=")) {
                    for (String part : qs.split("&")) {
                        if (part.startsWith("token=")) {
                            token = part.substring(6);
                            break;
                        }
                    }
                }
            }
        }

        if (token == null || token.isBlank()) {
            log.warn("WebSocket handshake without token");
            return false;
        }

        var maybeUser = authService.validateToken("Bearer " + token);
        if (maybeUser.isEmpty()) {
            log.warn("WebSocket handshake with invalid token");
            return false;
        }

        // store user info for later use
        attributes.put("user", maybeUser.get());
        attributes.put("token", token);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
