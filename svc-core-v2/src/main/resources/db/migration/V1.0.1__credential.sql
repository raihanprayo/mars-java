create table t_role
(
    id         varchar(37) primary key,
    name       varchar(100) unique not null,

    created_at timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_at timestamp(0)
);

create table t_account
(
    id         varchar(37) primary key,
    nik        varchar(30) unique not null,
    password   text unique,

    name       varchar(100)       not null,
    active     bool               not null default false,

    email      varchar(100),
    witel      varchar(15)        not null,
    sto        varchar(10),
    telegram   bigint,

    created_by varchar(100)       not null,
    created_at timestamp(0)       not null default CURRENT_TIMESTAMP,
    updated_by varchar(100),
    updated_at timestamp(0)
);

create table t_account_credential
(
    id             serial primary key,
    account_id     varchar(37)  not null,

    algo           varchar(30)  not null,
    secret         text,
    hash_iteration int4,

    password       text,
    priority       int4         not null,

    created_at     timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at     timestamp(0),
    constraint fk_account_id foreign key (account_id) references t_account (id)
);
create table t_account_expired
(
    id         serial primary key,
    account_id varchar(37)  not null,

    active     bool         not null,
    date       timestamp(0),

    created_at timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at timestamp(0),
    constraint fk_account_id foreign key (account_id) references t_account (id)
);

create table t_account_role
(
    id         serial primary key,
    role_id    varchar(37) not null,
    account_id varchar(37) not null,
    constraint fk_account_id foreign key (account_id) references t_account (id),
    constraint fk_role_id foreign key (role_id) references t_role (id)
);