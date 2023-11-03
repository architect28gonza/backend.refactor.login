create table tbl_tipo_dato (
    tip_id serial primary key,
    tip_tipo varchar(25) not null,
    tip_nombre varchar(25) not null,
    tip_estado boolean default true,
    tip_registro timestamp default now()
);

create table tbl_formulario (
    for_id serial primary key,
    for_dependencia int not null,
    for_prefijo varchar(100) not null,
    for_nombre varchar(100) not null,
    for_estado boolean default true,
    for_registro timestamp default now()
);

create table tbl_enfermedad (
    enf_id serial primary key,
    enf_codigo varchar(10) not null,
    enf_tabla varchar(50) not null,
    enf_nombre varchar(50) not null,
    enf_estado boolean default true,
    enf_registro timestamp default now()
);

create table tbl_categoria (
    cat_id serial primary key,
    cat_titulo varchar(100) not null,
    cat_subtitulo varchar(100) not null,
    cat_orden int not null,
    cat_estado boolean default true,
    cat_registro timestamp default now()
);

create table tbl_variable (
    var_id serial primary key,
    for_id int not null,
    tip_id int not null,
    cat_id int not null,
    var_nombre varchar(100) not null,
    var_etiqueta varchar(150) not null,
    var_longitud int not null,
    var_valminino int default 0 not null,
    var_valmaximo int default 0 not null,
    var_editar boolean default true not null,
    var_estado boolean default true not null,
    var_registro timestamp default now(),
    foreign key (for_id) references tbl_formulario (for_id),
    foreign key (tip_id) references tbl_tipo_dato (tip_id),
    foreign key (cat_id) references tbl_categoria (cat_id)
);

create table tbl_dependencia (
    dep_id serial primary key,
    for_id int not null,
    enf_id int not null,
    dep_dependiente int not null,
    dep_independiente int not null,
    dep_estado boolean default true,
    dep_registro timestamp default now(),
    foreign key (for_id) references tbl_formulario (for_id),
    foreign key (enf_id) references tbl_enfermedad (enf_id)
);

create table tbl_malla_validacion_operacion ( 
    mal_id serial primary key,
    var_id int not null, 
    mal_operacion varchar(300) not null,
    mal_estado boolean default true,
    mal_registro timestamp default now(),
    foreign key (var_id) references tbl_variable (var_id)
);

create table tbl_infvaraible_enfermedad (
    inf_id serial primary key,
    var_id int not null,
    enf_id int not null,
    inf_obligatorio boolean default false,
    inf_orden int not null,
    inf_valdefecto varchar(100) not null,
    inf_estado boolean default true,
    inf_registro timestamp default now(),
    foreign key (var_id) references tbl_variable (var_id),
    foreign key (enf_id) references tbl_enfermedad (enf_id)
);
