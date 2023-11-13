package com.refactor.login.security.services;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.refactor.login.dto.ResponseMessageDto;
import com.refactor.login.security.repository.RecuperacionRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ContrasenaService {

    private final RecuperacionRepository recuperacionRepository;

    public Mono<ResponseMessageDto> setValidarCodigoEnviado(int codigo) {
        return this.recuperacionRepository.findByCodigo(codigo)
                .flatMap(cod -> {
                    ResponseMessageDto responseMessageDto = new ResponseMessageDto();
                    String hora = cod.getRec_caducacion().split(" ")[1];
                    LocalTime horaActual = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    String horaFormateada = horaActual.format(formatter);

                    LocalTime hora1 = LocalTime.parse(hora, formatter);
                    LocalTime hora2 = LocalTime.parse(horaFormateada, formatter);

                    responseMessageDto.setStatus((hora1.plusMinutes(5).isBefore(hora2))
                            ? HttpStatus.OK
                            : HttpStatus.NOT_ACCEPTABLE);
                    responseMessageDto.setMessage((hora1.plusMinutes(5).isBefore(hora2))
                            ? "Por favor ingrese su nueva contrasena."
                            : "Acción no permitida, No se puede realizar el cambio puesto que ya vencio el código.");
                    return Mono.just(responseMessageDto);
                }).switchIfEmpty(Mono.just(new ResponseMessageDto()));
    }
}
