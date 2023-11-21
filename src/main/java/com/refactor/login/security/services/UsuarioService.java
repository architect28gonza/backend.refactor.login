package com.refactor.login.security.services;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static com.refactor.login.enums.Roles.USUARIO;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.refactor.login.dto.RequestContrasenaDto;
import com.refactor.login.dto.RequestUsuarioDto;
import com.refactor.login.dto.ResponseMessageDto;
import com.refactor.login.dto.ResponseTokenCreacionDto;
import com.refactor.login.dto.RequestInitUsuarioDto;
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

   private static final int TIEMPO_TOKEN = 86400000;

   public Mono<ResponseTokenCreacionDto> setGuardarUsuario(RequestUsuarioDto usuario) {
      return this.isExisteUsuario(usuario.getUsuario()).flatMap(
            isUsuarioExiste -> {
               if (isUsuarioExiste) {
                  return Mono.error(new ExceptionMain(BAD_REQUEST,
                        "Accion no completada, El usuario ya se encuentra registrado."));
               } else {
                  return this.usuarioRepository.save(this.getBuilderUsuario(usuario)).map(savedUser -> {
                     var token = this.jsonWebTokenService.setGenerarToken(new HashMap<>(),
                           savedUser.getUsername(), TIEMPO_TOKEN);
                     ResponseTokenCreacionDto tokenCreacionDto = new ResponseTokenCreacionDto();
                     tokenCreacionDto.setMessage("El usuario ha sido creado con exito");
                     tokenCreacionDto.setUsuario(savedUser.getUsername());
                     tokenCreacionDto.setStatus(HttpStatus.OK);
                     tokenCreacionDto.setToken(token);
                     this.notificacionService.addSubcripcion(usuario.getTelefono());
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

   private Mono<Boolean> isExisteUsuario(String usuario, String phoneOrEmail) {
      return this.usuarioRepository.findByIdAndEstadoAndPhoneOrEmail(usuario, true, phoneOrEmail).hasElement();
   }

   public Mono<ResponseTokenCreacionDto> setIniciarSesion(RequestInitUsuarioDto usuario) {
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
                                    usu.getUsername(), TIEMPO_TOKEN);
                              ResponseTokenCreacionDto tokenCreacionDto = new ResponseTokenCreacionDto();
                              tokenCreacionDto.setMessage("El inicio se ha completado con exito");
                              tokenCreacionDto.setUsuario(usu.getUsername());
                              tokenCreacionDto.setToken(token);
                              tokenCreacionDto.setStatus(HttpStatus.OK);
                              return Mono.just(tokenCreacionDto);
                           } else {
                              return Mono.error(
                                    new ExceptionMain(BAD_REQUEST,
                                          "No se puede autenticar, Credenciales incorrectas."));
                           }
                        });
               }
            });
   }

   public Mono<ResponseMessageDto> setTipoRecuperacion(RequestContrasenaDto cambiarContrasena) {
      return this.isExisteUsuario(cambiarContrasena.getUsuario(), cambiarContrasena.getEmailOrPhone()).flatMap(
            isUsuarioExiste -> {
               if (!isUsuarioExiste) {
                  return Mono.error(new ExceptionMain(BAD_REQUEST,
                        "La accion no se puede completar, Los datos proporcionados no son correctos."));
               } else {
                  boolean isCorreo = cambiarContrasena.isOperacionEmail();
                  String tipoEnvio = (isCorreo ? "correo" : "teléfono");

                  return this.getTipoEnvio(cambiarContrasena).flatMap(
                        cod -> {
                           ResponseMessageDto responseMessageDto = new ResponseMessageDto();
                           responseMessageDto.setMessage((cod.getStatus() == HttpStatus.OK)
                                 ? "Favor ingresar el código que le fue enviado a su "
                                       + tipoEnvio + " al momento de registrar su cuenta"
                                 : "No fue posible enviar el código, Por favor intente mas tarde.");

                           responseMessageDto.setStatus((cod.getStatus() == HttpStatus.OK)
                                 ? HttpStatus.OK
                                 : HttpStatus.INTERNAL_SERVER_ERROR);

                           return Mono.just(responseMessageDto);
                        });
                  // String resultadoEnvio = (isCorreo)
                  // ?
                  // this.envioCorreoService.setEnviarCorreo(cambiarContrasena.getEmailOrPhone())
                  // : this.notificacionService.setEnviarSMS(cambiarContrasena.getEmailOrPhone());
               }
            });
   }

   private Mono<ResponseMessageDto> getTipoEnvio(RequestContrasenaDto requestEnvio) {
      return (!requestEnvio.isOperacionEmail())
            ? this.notificacionService.setEnviarSMS(requestEnvio.getEmailOrPhone(), requestEnvio.getUsuario())
            : Mono.just(new ResponseMessageDto());
   }
}
