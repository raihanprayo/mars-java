create table t_sto
(
    id    serial primary key,
    code  varchar(5) unique not null,
    name  varchar(50),
    witel varchar(20)       not null,
    datel varchar(50)       not null
);