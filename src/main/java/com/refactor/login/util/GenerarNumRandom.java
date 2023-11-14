package com.refactor.login.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.refactor.login.security.entity.RecuperacionEntity;

public class GenerarNumRandom {

    private GenerarNumRandom() {
    }

    public static int getNumeroRandom() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(1000000) + 1;
    }

    public static RecuperacionEntity setInsertCodigo(int codigo, String usuario) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime horaCon5Minutos = ahora.plusMinutes(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return RecuperacionEntity.builder()
                .rec_codigo(codigo)
                .rec_usuario(usuario)
                .rec_caducacion(horaCon5Minutos.format(formatter))
                .build();
    }
}
