package com.refactor.login.validation;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.stereotype.Component;

import com.refactor.login.exception.ExceptionMain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class ObjectValidation {
    private final Validator validator;

    @SneakyThrows
    public <T> T validate(T object) {
        Set<ConstraintViolation<T>> errores = validator.validate(object);
        if (errores.isEmpty()) {
            return object;
        } else {
            String message = errores
                    .stream()
                    .map(error -> error.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ExceptionMain(BAD_REQUEST, message);
        }
    }
}
