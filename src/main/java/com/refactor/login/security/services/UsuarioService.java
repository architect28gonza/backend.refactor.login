package com.refactor.login.security.services;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static com.refactor.login.enums.Roles.USUARIO;

import java.util.HashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.refactor.login.dto.RequestContrasenaDto;
import com.refactor.login.dto.RequestUsuarioDto;
import com.refactor.login.dto.ResponseMessageDto;
import com.refactor.login.dto.TokenCreacionDto;
import com.refactor.login.dto.UsuarioDto;
import com.refactor.login.exception.ExceptionMain;
import com.refactor.login.security.entity.UsuarioEntity;
import com.refactor.login.security.repository.UsuarioRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UsuarioService {

   private final PasswordEncoder codificarContrasena;

   private final UsuarioRepository usuarioRepository;

   private final JsonWebTokenService jsonWebTokenService;

   private final NotificacionService notificacionService;

   public Mono<TokenCreacionDto> setGuardarUsuario(RequestUsuarioDto usuario) {
      return this.isExisteUsuario(usuario.getUsuario()).flatMap(
            isUsuarioExiste -> {
               if (isUsuarioExiste) {
                  return Mono.error(new ExceptionMain(BAD_REQUEST,
                        "Accion no completada, El usuario ya se encuentra registrado."));
               } else {
                  return this.usuarioRepository.save(this.getBuilderUsuario(usuario)).map(savedUser -> {
                     var token = this.jsonWebTokenService.setGenerarToken(new HashMap<>(),
                           savedUser.getUsername());
                     TokenCreacionDto tokenCreacionDto = new TokenCreacionDto();
                     tokenCreacionDto.setUsuario(savedUser.getUsername());
                     tokenCreacionDto.setToken(token);
                     // this.notificacionService.addSubcripcionUsuario(usuario.getCorreo());
                     return tokenCreacionDto;
                  });
               }
            });
   }

   private UsuarioEntity getBuilderUsuario(RequestUsuarioDto usuario) {
      return UsuarioEntity.builder()
            .usu_usuario(usuario.getUsuario())
            .usu_contrasena(this.codificarContrasena.encode(usuario.getContrasena()))
            .usu_rol(USUARIO)
            .usu_telefono(usuario.getTelefono())
            .usu_email(usuario.getCorreo())
            .build();
   }

   private Mono<Boolean> isExisteUsuario(String usuario) {
      return this.usuarioRepository.findByUsuarioAndEstado(usuario, true).hasElement();
   }

   public Mono<TokenCreacionDto> setIniciarSesion(UsuarioDto usuario) {
      return this.isExisteUsuario(usuario.getUsuario()).flatMap(
            isUsuarioExiste -> {
               if (!isUsuarioExiste) {
                  return Mono.error(new ExceptionMain(BAD_REQUEST,
                        "Accion no completada, El usuario no se encuentra registrado."));
               } else {
                  Mono<UsuarioEntity> usuarioEncontrado = this.usuarioRepository
                        .findByUsuarioAndEstado(usuario.getUsuario(), true);
                  return usuarioEncontrado.flatMap(
                        usu -> {
                           if (this.codificarContrasena.matches(usuario.getContrasena(), usu.getPassword())) {
                              var token = this.jsonWebTokenService.setGenerarToken(new HashMap<>(),
                                    usu.getUsername());
                              TokenCreacionDto tokenCreacionDto = new TokenCreacionDto();
                              tokenCreacionDto.setUsuario(usu.getUsername());
                              tokenCreacionDto.setToken(token);
                              return Mono.just(tokenCreacionDto);
                           } else {
                              return Mono.error(
                                    new ExceptionMain(BAD_REQUEST, "No se puede autenticar, Contraseña incorrecta."));
                           }
                        });
               }
            });
   }

   public Mono<ResponseMessageDto> setCambiarContrasena(RequestContrasenaDto cambiarContrasena) {
      return this.isExisteUsuario(cambiarContrasena.getUsuario()).flatMap(
            isUsuarioExiste -> {
               if (!isUsuarioExiste) {
                  return Mono.error(new ExceptionMain(BAD_REQUEST,
                        "La accion no se puede completar, El nombre de usuario no se encuentra."));
               } else {
                  // final boolean isCorreo = cambiarContrasena.isCorreo();
                  this.notificacionService.setEnviarSMS(cambiarContrasena.getEmailOrPhone());
                  ResponseMessageDto responseMessageDto = new ResponseMessageDto();
                  return Mono.just(responseMessageDto);
               }
            });
   }
}
