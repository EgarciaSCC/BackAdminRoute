package nca.scc.com.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SuppressWarnings("unused")
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        String[] origins = new String[0];
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            origins = allowedOrigins.split(",");
            for (int i = 0; i < origins.length; i++) {
                origins[i] = origins[i].trim();
            }
        }

        // If user set wildcard '*' or environment variable APP_CORS_ALLOW_ALL=true, allow any origin but disable credentials
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
            registry.addMapping("/api/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(false)
                    .maxAge(3600);
        } else {
            registry.addMapping("/api/**")
                    .allowedOrigins(origins)
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    }
}
