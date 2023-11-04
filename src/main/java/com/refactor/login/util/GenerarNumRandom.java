package com.refactor.login.util;

import java.security.SecureRandom;

public class GenerarNumRandom {
    
    private GenerarNumRandom (){}

    public static int getNumeroRandom() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(10000) + 1;
    }   
}
