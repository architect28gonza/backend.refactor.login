package com.refactor.login.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestContrasenaDto {
    private String usuario;
    private String emailOrPhone;
    private boolean operacionEmail;
}
