package nca.scc.com.admin.config;

import nca.scc.com.admin.config.filters.TenantFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final TenantFilter tenantFilter;

    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;

    public SecurityConfig(TenantFilter tenantFilter) {
        this.tenantFilter = tenantFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtProperties props,
                                                   @Value("${security.require-authenticated:true}") boolean requireAuthenticated) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> {
                // build CorsConfiguration similar to WebConfig
                CorsConfiguration config = new CorsConfiguration();

                String[] origins = new String[0];
                if (allowedOrigins != null && !allowedOrigins.isBlank()) {
                    origins = allowedOrigins.split(",");
                    for (int i = 0; i < origins.length; i++) {
                        origins[i] = origins[i].trim();
                    }
                }

                boolean hasWildcard = false;
                for (String o : origins) {
                    if ("*".equals(o)) {
                        hasWildcard = true;
                        break;
                    }
                }
                String envAllow = System.getenv("APP_CORS_ALLOW_ALL");
                if (envAllow != null && envAllow.equalsIgnoreCase("true")) {
                    hasWildcard = true;
                }

                if (hasWildcard) {
                    // allow any origin but disable credentials for wildcard
                    config.addAllowedOriginPattern("*");
                    config.setAllowCredentials(false);
                } else {
                    if (origins.length > 0) {
                        config.setAllowedOrigins(Arrays.asList(origins));
                    }
                    config.setAllowCredentials(true);
                }

                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                cors.configurationSource(source);
            });

        boolean jwtConfigured = (props.getJwkSetUri() != null && !props.getJwkSetUri().isBlank())
                || (props.getSecret() != null && !props.getSecret().isBlank());

        if (jwtConfigured) {
            // Build decoder based on available configuration
            JwtDecoder decoder;
            if (props.getJwkSetUri() != null && !props.getJwkSetUri().isBlank()) {
                decoder = NimbusJwtDecoder.withJwkSetUri(props.getJwkSetUri()).build();
            } else {
                SecretKey key = new SecretKeySpec(props.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
                decoder = NimbusJwtDecoder.withSecretKey(key).build();
            }

            // Converter: leer claim 'role' y crear GrantedAuthority
            JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
            jwtAuthConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
                Object roleClaim = jwt.getClaim("role");
                if (roleClaim == null) return Collections.emptyList();
                String role = roleClaim.toString();
                java.util.List<GrantedAuthority> authorities = java.util.List.of(new SimpleGrantedAuthority(role));
                return authorities;
            });

            http
                .authorizeHttpRequests(auth -> auth
                    // Public resources
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    // Swagger/OpenAPI public (UI + docs)
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/swagger-ui.html").permitAll()
                    .requestMatchers("/swagger-ui/index.html").permitAll()
                    // Require authentication only for entity API endpoints
                    .requestMatchers("/api/buses/**").authenticated()
                    .requestMatchers("/api/conductores/**").authenticated()
                    .requestMatchers("/api/coordinadores/**").authenticated()
                    .requestMatchers("/api/historial-rutas/**").authenticated()
                    .requestMatchers("/api/novedades/**").authenticated()
                    .requestMatchers("/api/pasajeros/**").authenticated()
                    .requestMatchers("/api/realtime/**").authenticated()
                    .requestMatchers("/api/rutas/**").authenticated()
                    .requestMatchers("/api/sedes/**").authenticated()
                    // Any other request: allow (do not require auth)
                    .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(decoder).jwtAuthenticationConverter(jwtAuthConverter)));

        } else {
            if (requireAuthenticated) {
                // En entornos donde se exige autenticación, arrancar sin JWT es un error: fallamos rápido
                throw new IllegalStateException("JWT no está configurado (jwt.jwk-set-uri o jwt.secret). Configure JWT para habilitar la validación de tokens.");
            }
            // No JWT configurado y no se requiere autenticación -> permitir todas las peticiones (modo desarrollo muy abierto)
            log.warn("No JWT configuration found (jwt.jwk-set-uri or jwt.secret). Security will allow all requests because security.require-authenticated=false.");
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        // add tenant filter after authentication
        http.addFilterAfter(tenantFilter, BasicAuthenticationFilter.class);

        // allow frames for h2-console
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
