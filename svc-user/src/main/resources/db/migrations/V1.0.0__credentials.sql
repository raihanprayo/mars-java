create table t_user
(
    id               varchar(37) primary key,
    nik              varchar(50) unique  not null,
    name             varchar(100) unique not null,
    email            varchar(100) unique,
    password         text                not null,
    phone            varchar(100),

    enabled          bool                not null default false,
    mars_witel       varchar(15)         not null,
    mars_sto         bigint,
    mars_tg_id       bigint,
    mars_tg_username varchar(100),

    created_by       varchar(50)         not null,
    created_at       timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_by       varchar(50),
    updated_at       timestamp(0)
);

create table t_role
(
    id         varchar(37) primary key,
    name       varchar(100) unique not null,
    "order"    int4 unique         not null,

    created_by varchar(50)         not null,
    created_at timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_by varchar(50),
    updated_at timestamp(0)
);

create table t_user_roles
(
    id          serial primary key,
    ref_user_id varchar(37) not null,
    ref_role_id varchar(37) not null,

    constraint fk_ref_user_id foreign key (ref_user_id) references t_user (id),
    constraint fk_ref_role_id foreign key (ref_role_id) references t_role (id)
);