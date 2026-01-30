package nca.scc.com.admin.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private final JwtProperties props;

    public JwtUtils(JwtProperties props) {
        this.props = props;
    }

    public String extractTenant(Authentication auth) {
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            Object claim = jwt.getClaim(props.getClaimTenant());
            return claim != null ? claim.toString() : null;
        }
        return null;
    }
}
