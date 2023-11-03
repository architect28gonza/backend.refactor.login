package com.refactor.login.security.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.refactor.login.interfaces.UsuarioInterface;
import com.refactor.login.security.entity.UsuarioEntity;

import reactor.core.publisher.Mono;

public interface UsuarioRepository extends ReactiveCrudRepository<UsuarioEntity, Integer> {
    
    @Query("SELECT * FROM ref_autenticacion.tbl_usuario WHERE usu_usuario = :id AND usu_estado = :estado")
    Mono<UsuarioEntity> findByUsuarioAndEstado(String usuario, boolean estado);

    @Query("SELECT usu_usuario FROM ref_autenticacion.tbl_usuario WHERE usu_id = :id AND usu_estado = :estado")
    Mono<UsuarioInterface> findByIdAndEstado(int idUsuario, boolean estado);
}
