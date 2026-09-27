package com.example.wiki.util;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.example.wiki.security.rest.SecurityRestConfiguration;

@ConditionalOnBean(SecurityRestConfiguration.class)
@Configuration
@ConfigurationProperties(prefix = "jwt", ignoreInvalidFields = true)
public class JwtProperties {
    private String secret = UUID.randomUUID().toString();

    private String issuer = UUID.randomUUID().toString();

    private Long expiration = 86400000L; // 24h

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
}
