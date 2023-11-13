package com.refactor.login.handlers;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.refactor.login.dto.RequestCodigoDto;
import com.refactor.login.dto.ResponseMessageDto;
import com.refactor.login.security.services.ContrasenaService;
import com.refactor.login.validation.ObjectValidation;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContrasenaHandler {

    private final ObjectValidation objectValidation;

    private final ContrasenaService contrasenaService;

    public Mono<ServerResponse> setVerificarCodigoContrasena(ServerRequest request) {
        return request.bodyToMono(RequestCodigoDto.class)
                .doOnNext(objectValidation::validate).flatMap(usuario -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON).body(
                                this.contrasenaService.setValidarCodigoEnviado(usuario.getCodigo()),
                                ResponseMessageDto.class));
    }

}
