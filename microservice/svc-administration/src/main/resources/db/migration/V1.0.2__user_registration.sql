create table t_user_registration
(
    id          varchar(37) primary key,
    no          varchar(50) unique not null,
    status      varchar(30)        not null,

    name        varchar(100)       not null,
    telegram_id bigint,
    nik         varchar(100)       not null,
    phone       varchar(50)        not null,
    witel       varchar(15)        not null,
    sto         varchar(10),

    created_at  timestamp(3)       not null default current_timestamp,
    updated_at  timestamp(3),

    constraint fk_sto_code foreign key (sto) references t_sto (code)
);