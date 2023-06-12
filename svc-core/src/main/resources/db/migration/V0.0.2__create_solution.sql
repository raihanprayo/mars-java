create table t_solution
(
    id          serial primary key,
    name        varchar(100) unique not null,
    description text,
    product     varchar(15),

    created_at  timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_at  timestamp(0)                 default CURRENT_TIMESTAMP
);