package com.refactor.login.security.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ConfigConvertidorAutenticacion implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION))
        .filter(token -> token.startsWith("Bearer "))
        .map(token -> token.substring(7))
        .map(token -> new ConfigAutenticacionToken(token));
    }
}
