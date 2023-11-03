package com.refactor.login.security.entity;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.refactor.login.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_usuario")
public class UsuarioEntity implements UserDetails {

    @Id
    private int usu_id;

    private String usu_usuario;

    private String usu_contrasena;

    private Roles usu_rol;

    private String usu_telefono;

    private String usu_email;

    @Builder.Default
    private boolean usu_estado = true;

    @Builder.Default
    private Timestamp usu_registro = new Timestamp(System.currentTimeMillis());

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(usu_rol.name()));
    }

    @Override
    public String getPassword() {
        return this.usu_contrasena;
    }

    @Override
    public String getUsername() {
        return this.usu_usuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
