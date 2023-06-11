create table t_sto
(
    id         varchar(10) primary key,
    name       varchar(50) unique,

    witel      varchar(20)  not null,
    datel      varchar(50)  not null,

    created_at timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at timestamp(0)
);