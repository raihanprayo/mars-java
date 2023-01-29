create table t_solution
(
    id          serial primary key,
    name        varchar(100) unique not null,
    description text,

    created_at  timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_at  timestamp(0)                 default CURRENT_TIMESTAMP
);

alter table t_ticket_agent
    add column solution varchar(100);