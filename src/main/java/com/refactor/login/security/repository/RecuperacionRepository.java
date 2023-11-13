package com.refactor.login.security.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.refactor.login.security.entity.RecuperacionEntity;

import reactor.core.publisher.Mono;

public interface RecuperacionRepository extends ReactiveCrudRepository<RecuperacionEntity, Integer> {

    @Query("SELECT * FROM ref_autenticacion.tbl_recuperacion WHERE rec_codigo = :codigo AND rec_estado = true")
    Mono<RecuperacionEntity> findByCodigo(int codigo);

}
