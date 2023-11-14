package com.refactor.login.security.services;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.refactor.login.dto.ResponseMessageDto;
import com.refactor.login.dto.ResponseTokenCreacionDto;
import com.refactor.login.dto.UsuarioDto;
import com.refactor.login.security.repository.RecuperacionRepository;
import com.refactor.login.security.repository.UsuarioRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ContrasenaService {

    private final RecuperacionRepository recuperacionRepository;

    private final UsuarioRepository usuarioRepository;

    private final JsonWebTokenService jsonWebTokenService;

    private static final int TIEMPO_TOKEN = 5 * 60;

    public Mono<ResponseTokenCreacionDto> setValidarCodigoEnviado(int codigo, String usuario) {
        return this.recuperacionRepository.findByCodigo(codigo)
                .flatMap(cod -> {
                    ResponseTokenCreacionDto response = new ResponseTokenCreacionDto();
                    String hora = cod.getRec_caducacion().split(" ")[1];
                    LocalTime horaActual = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String horaFormateada = horaActual.format(formatter);

                    LocalTime hora1 = LocalTime.parse(hora, formatter);
                    LocalTime hora2 = LocalTime.parse(horaFormateada, formatter);

                    response.setStatus((hora1.plusMinutes(5).isBefore(hora2))
                            ? HttpStatus.OK
                            : HttpStatus.NOT_ACCEPTABLE);
                    response.setMessage((hora1.plusMinutes(5).isBefore(hora2))
                            ? "Por favor ingrese su nueva contrasena."
                            : "Acción no permitida, No se puede realizar el cambio puesto que ya vencio el código.");

                    var token = this.jsonWebTokenService.setGenerarToken(new HashMap<>(), usuario, TIEMPO_TOKEN);
                    response.setToken(token);

                    return Mono.just(response);
                }).switchIfEmpty(Mono.just(new ResponseTokenCreacionDto()));
    }

    @Transactional
    public Mono<ResponseMessageDto> setCambiarContrasena(UsuarioDto usuario) {
        return this.usuarioRepository.findByUsuarioAndEstado(usuario.getUsuario(), true)
                .flatMap(
                        user -> {
                            user.setUsu_contrasena(usuario.getContrasena());
                            this.usuarioRepository.save(user);
                            ResponseMessageDto responseMessageDto = new ResponseMessageDto();
                            responseMessageDto.setMessage("Cambio de contraseña completado con exito");
                            responseMessageDto.setStatus(HttpStatus.OK);
                            return Mono.just(responseMessageDto);
                        })
                .switchIfEmpty(Mono.just(new ResponseMessageDto()));
    }

}
