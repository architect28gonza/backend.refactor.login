package com.refactor.login.handlers;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.refactor.login.dto.UsuarioDto;
import com.refactor.login.security.services.RedSocialService;
import com.refactor.login.validation.ObjectValidation;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AutenticacionRedSocialHandler {
    
    private final RedSocialService redSocialService;

    private final ObjectValidation objectValidation;

    public Mono<ServerResponse> setGuardarUsuario(ServerRequest request) {
        return request.bodyToMono(UsuarioDto.class)
                .doOnNext(objectValidation::validate).flatMap(usuario -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON).body(
                                this.redSocialService.iniciarSesionRedSocial(), String.class));
    }

}
