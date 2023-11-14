package com.refactor.login.security.entity;

import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_recuperacion")
public class RecuperacionEntity {
    
    @Id
    private int rec_id;
    private int rec_codigo;
    private String rec_usuario;
    private String rec_caducacion;
    
    @Builder.Default
    private boolean rec_estado = true;

    @Builder.Default
    private Timestamp rec_registro = new Timestamp(System.currentTimeMillis());

}
