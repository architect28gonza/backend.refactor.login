package com.refactor.login.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.refactor.login.handlers.ContrasenaHandler;

@Configuration
public class ContrasenaRouter {
    
    private static final StringBuilder ENDPOINT = new StringBuilder("/api/v1");

    @Bean
    RouterFunction<ServerResponse> routerContrasena(ContrasenaHandler handler) {
        return RouterFunctions
                .route()
                .POST(ENDPOINT.toString().concat("/codigo"), handler::setVerificarCodigoContrasena)
                .POST(ENDPOINT.toString().concat("/cambiar"), handler::setCambiarContrasena)
                .build();
    }

}
