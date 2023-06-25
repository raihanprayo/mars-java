create table t_sto
(
    code       varchar(5) primary key,
    name       varchar(50),
    witel      varchar(20)  not null,
    datel      varchar(50)  not null,

    created_by varchar(100) not null,
    created_at timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_by varchar(100),
    updated_at timestamp(0)          default CURRENT_TIMESTAMP
);