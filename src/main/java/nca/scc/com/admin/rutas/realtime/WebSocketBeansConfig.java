package nca.scc.com.admin.rutas.realtime;

import nca.scc.com.admin.rutas.auth.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketBeansConfig {

    @Bean
    public WebSocketAuthHandshakeInterceptor webSocketAuthHandshakeInterceptor(AuthService authService) {
        return new WebSocketAuthHandshakeInterceptor(authService);
    }

    @Bean
    public CustomHandshakeHandler customHandshakeHandler() {
        return new CustomHandshakeHandler();
    }
}
