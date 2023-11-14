package com.refactor.login.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTokenCreacionDto {
    
    private String message;
    private String usuario;
    private String token;
    private HttpStatus status;
}
