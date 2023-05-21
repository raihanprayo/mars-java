create table t_sto
(
    id    serial primary key,
    witel varchar(15)         not null,
    datel varchar(50)         not null,
    name  varchar(100) unique not null,
    alias varchar(50) unique  not null
);