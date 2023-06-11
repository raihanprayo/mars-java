create table t_user
(
    id         varchar(37) primary key,
    name       varchar(255)       not null,
    nik        varchar(50) unique not null,
    tg_id      bigint unique,

    phone      varchar(50),
    witel      varchar(15),
    sto        varchar(10),
    email      varchar(100) unique,
    password   text,

    enabled    bool                        default false,

    created_at timestamp(0)       not null default CURRENT_TIMESTAMP,
    created_by varchar(255)       not null,
    updated_at timestamp(0)                default CURRENT_TIMESTAMP,
    updated_by varchar(255),

    constraint fk_sto_id foreign key (sto) references t_sto (id)
);

create table t_user_approval
(
    id          varchar(37) primary key,
    no          varchar(100) unique not null,
    status      varchar(30),
    name        varchar(100)        not null,
    nik         varchar(100)        not null,
    phone       varchar(30)         not null,
    witel       varchar(15)         not null,
    sto         varchar(7),

    tg_id       bigint,
    tg_username varchar(50),

    created_at  timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_at  timestamp(0)                 default CURRENT_TIMESTAMP
);