package com.refactor.login.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import static com.refactor.login.contants.RequestConst.ENDPOINT;

@Configuration
@EnableWebFlux
public class ConfigCors implements WebFluxConfigurer {

    @Value("${util.application.front}")
    private String origen;

    @Value("${util.application.methods}")
    private String[] metodos;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(ENDPOINT.concat("/**"))
                .allowedOrigins(origen)
                .allowedMethods(metodos)
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
