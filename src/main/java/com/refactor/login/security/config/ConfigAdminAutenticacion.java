package com.refactor.login.security.config;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import com.refactor.login.exception.ExceptionMain;
import com.refactor.login.security.entity.UsuarioEntity;
import com.refactor.login.security.repository.UsuarioRepository;
import com.refactor.login.security.services.JsonWebTokenService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConfigAdminAutenticacion implements ReactiveAuthenticationManager {

    private final JsonWebTokenService jsonWebTokenService;

    private final UsuarioRepository usuarioRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(ConfigAutenticacionToken.class)
                .flatMap(autenticacion -> {
                    String nombreUsuario = this.jsonWebTokenService
                            .extraerNombreUsuarioJwt(autenticacion.getCredentials());
                    Mono<UsuarioEntity> usuarioEncontrado = usuarioRepository.findByUsuarioAndEstado(nombreUsuario, true);

                    return usuarioEncontrado.flatMap(usuario -> {
                        if (usuario.getUsername() == null) {
                            Mono.error(new ExceptionMain(NOT_FOUND, "El usuario administrado no fue encontrado"));
                        }
                        if (this.jsonWebTokenService.isTokenValido(autenticacion.getCredentials(),
                                usuario.getUsername())) {
                            return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(
                                    usuario.getUsername(), usuario.getPassword(), usuario.getAuthorities()));
                        }
                        Mono.error(new IllegalArgumentException("Proceso no completado, Token invalido"));
                        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(
                                usuario.getUsername(), usuario.getPassword(), usuario.getAuthorities()));
                    });
                });
    }
}
