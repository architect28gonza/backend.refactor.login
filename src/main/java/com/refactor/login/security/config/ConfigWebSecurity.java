package com.refactor.login.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

import static com.refactor.login.contants.RequestConst.ENDPOINT;
import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

@Configuration
@EnableReactiveMethodSecurity
public class ConfigWebSecurity {

	private final String[] endpoints = {
			ENDPOINT.concat("/registrar"),
			ENDPOINT.concat("/login"),
			ENDPOINT.concat("/recuperar")
	};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(
			ServerHttpSecurity http,
			ConfigConvertidorAutenticacion convertidorAutenticacion,
			ConfigAdminAutenticacion adminAutenticacion) {

		AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(adminAutenticacion);
		jwtFilter.setServerAuthenticationConverter(convertidorAutenticacion);
		return http
				.authorizeExchange(
						auth -> {
							auth.pathMatchers(endpoints).permitAll();
							auth.anyExchange().authenticated();
						})
				.addFilterAt(jwtFilter, AUTHENTICATION)
				.httpBasic().disable()
				.formLogin().disable()
				.csrf().disable()
				.cors().disable()
				.build();
	}
}
