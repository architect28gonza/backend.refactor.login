package com.refactor.login.security.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class ConfigAutenticacionToken extends AbstractAuthenticationToken {

    private final String token;

    public ConfigAutenticacionToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
    }

    @Override
    public String getCredentials() {
        return this.token;
    }

    @Override
    public String getPrincipal() {
        return this.token;
    }
    
}
