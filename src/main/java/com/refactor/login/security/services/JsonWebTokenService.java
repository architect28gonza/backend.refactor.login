package com.refactor.login.security.services;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.vavr.control.Try;

@Service
public class JsonWebTokenService {

    @Value("${util.application.jwt}")
    private String secrectToken;

    private static final int TIEMPO_TOKEN = 86400000;

    public String extraerNombreUsuarioJwt(String token) {
        return this.extraerClaims(token, Claims::getSubject);
    }

    private Key getFirmaClave() {
        byte[] keyBytes = Decoders.BASE64.decode(secrectToken);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extraerAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getFirmaClave())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extraerClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extraerAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String setGenerarToken(Map<String, Object> claims, String nombreUsuario) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(nombreUsuario)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_TOKEN))
                .signWith(this.getFirmaClave(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValido(String token, String nombreUsuario) {
        final String usuario = this.extraerNombreUsuarioJwt(token);
        return (usuario.equals(nombreUsuario)) && !this.isTokenExpirado(token);
    }

    public boolean isTokenExpirado(String token) {
        return Try.of(() -> this.extraerExpiracionToken(token).before(new Date())).isSuccess();
    }

    public Date extraerExpiracionToken(String token) {
        return this.extraerClaims(token, Claims::getExpiration);
    }
}
