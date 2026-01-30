package nca.scc.com.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String header = "Authorization";
    private String authPrefix = "Bearer";
    private String claimTenant = "tid";
    private String secret;
    private String jwkSetUri;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getAuthPrefix() {
        return authPrefix;
    }

    public void setAuthPrefix(String authPrefix) {
        this.authPrefix = authPrefix;
    }

    public String getClaimTenant() {
        return claimTenant;
    }

    public void setClaimTenant(String claimTenant) {
        this.claimTenant = claimTenant;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getJwkSetUri() {
        return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }
}
