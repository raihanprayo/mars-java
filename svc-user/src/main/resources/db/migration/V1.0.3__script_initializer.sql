create table t_script_init
(
    id       varchar(50) primary key,
    executed bool not null,
    message  text
);