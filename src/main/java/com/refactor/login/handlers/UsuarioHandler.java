package com.refactor.login.handlers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.refactor.login.dto.RequestContrasenaDto;
import com.refactor.login.dto.RequestUsuarioDto;
import com.refactor.login.dto.ResponseMessageDto;
import com.refactor.login.dto.UsuarioDto;
import com.refactor.login.security.entity.UsuarioEntity;
import com.refactor.login.security.services.UsuarioService;
import com.refactor.login.validation.ObjectValidation;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsuarioHandler {

    private final UsuarioService usuarioService;

    private final ObjectValidation objectValidation;

    public Mono<ServerResponse> setGuardarUsuario(ServerRequest request) {
        return request.bodyToMono(RequestUsuarioDto.class)
                .doOnNext(objectValidation::validate).flatMap(usuario -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON).body(
                                this.usuarioService.setGuardarUsuario(usuario), UsuarioEntity.class));
    }

    public Mono<ServerResponse> setIniciarSesionUsuario(ServerRequest request) {
        return request.bodyToMono(UsuarioDto.class)
                .doOnNext(objectValidation::validate).flatMap(usuario -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON).body(
                                this.usuarioService.setIniciarSesion(usuario), UsuarioEntity.class));
    }


    public Mono<ServerResponse> setTipoRecuperacionContrasena(ServerRequest request) {
        return request.bodyToMono(RequestContrasenaDto.class)
                .doOnNext(objectValidation::validate).flatMap(contrasena -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON).body(
                                this.usuarioService.setTipoRecuperacion(contrasena), ResponseMessageDto.class));
    }
}
