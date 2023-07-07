create table t_sto
(
    code       varchar(5) primary key,
    name       varchar(50) unique not null,
    witel      varchar(20)        not null,
    datel      varchar(50)        not null,

    created_at timestamp(0)       not null default current_timestamp,
    created_by varchar(50)        not null,

    updated_at timestamp(0),
    updated_by varchar(50)
);