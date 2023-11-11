package com.refactor.login.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.refactor.login.handlers.UsuarioHandler;

@Configuration
public class AuthRouter {
    private static final StringBuilder ENDPOINT = new StringBuilder("/api/v1");

    @Bean
    RouterFunction<ServerResponse> routerPersona(UsuarioHandler handler) {
        return RouterFunctions
                .route()
                .POST(ENDPOINT.toString().concat("/registrar"), handler::setGuardarUsuario)
                .POST(ENDPOINT.toString().concat("/login"), handler::setIniciarSesionUsuario)
                .POST(ENDPOINT.toString().concat("/recuperar"), handler::setTipoRecuperacionContrasena)
                .build();
    }
}
