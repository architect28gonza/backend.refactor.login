package com.refactor.login.dto;

import org.apache.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMessageDto {
    private String message;
    private HttpStatus status;
}
