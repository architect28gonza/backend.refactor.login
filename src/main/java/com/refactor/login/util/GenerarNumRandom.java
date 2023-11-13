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
        return secureRandom.nextInt(10000) + 1;
    }

    public static RecuperacionEntity setInsertCodigo(int codigo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime horaCon5Minutos = ahora.plusMinutes(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String horaCon5MinutosStr = horaCon5Minutos.format(formatter);

        return RecuperacionEntity.builder()
                .rec_codigo(codigo)
                .rec_caducacion(horaCon5MinutosStr)
                .build();
    }
}
