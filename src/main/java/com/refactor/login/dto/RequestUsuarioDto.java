package com.refactor.login.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUsuarioDto {
    private String usuario;
    private String contrasena;
    private String telefono;
    private String correo;
}