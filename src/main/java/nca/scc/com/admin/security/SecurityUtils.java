package nca.scc.com.admin.security;

import nca.scc.com.admin.rutas.auth.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {

    public static Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt) {
            return (Jwt) auth.getPrincipal();
        }
        return null;
    }

    public static String getTenantClaim(String claimName) {
        Jwt jwt = getJwt();
        if (jwt == null) return null;
        Object c = jwt.getClaim(claimName);
        return c == null ? null : c.toString();
    }

    public static Role getRoleClaim() {
        Jwt jwt = getJwt();
        if (jwt == null) return null;
        Object c = jwt.getClaim("role");
        if (c == null) return null;
        try {
            return Role.valueOf(c.toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
