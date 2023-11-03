
CREATE SCHEMA ref_autenticacion;
SET search_path TO ref_autenticacion;
create table tbl_usuario(
    usu_id serial primary key,
    usu_usuario varchar(100) not null,
    usu_contrasena varchar(250) not null,
    usu_rol varchar(50) not null,
    usu_telefono varchar(20) not null,
    usu_email varchar(50) not null,
    usu_estado boolean default true,
    usu_registro timestamp default now()
);
