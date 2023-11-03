package com.refactor.login.exception;

import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {

    public ExceptionHandler(
            ErrorAttributes errorAttributes,
            Resources resources,
            ApplicationContext applicationContext,
            ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageReaders(codecConfigurer.getReaders());
        this.setMessageWriters(codecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrrorResponse);
    }

    private Mono<ServerResponse> renderErrrorResponse(ServerRequest request) {
        Map<String, Object> errorMap = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        HttpStatus status = (HttpStatus) Optional
                .ofNullable(errorMap.get("status"))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorMap));
    }
}
