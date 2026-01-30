package nca.scc.com.admin.config.filters;

import nca.scc.com.admin.config.JwtProperties;
import nca.scc.com.admin.rutas.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    private final JwtProperties jwtProperties;

    public TenantFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String tenant = null;

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) auth.getPrincipal();
                Object claim = jwt.getClaim(jwtProperties.getClaimTenant());
                if (claim != null) {
                    tenant = claim.toString();
                }
            }

            if (tenant == null || tenant.isEmpty()) {
                String fallback = request.getHeader("X-Tenant-ID");
                if (fallback != null && !fallback.isEmpty()) {
                    tenant = fallback;
                }
            }

            if (tenant != null && !tenant.isEmpty()) {
                TenantContext.setCurrentTenant(tenant);
                log.debug("Tenant set to {}", tenant);
            } else {
                log.debug("No tenant found in request");
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
