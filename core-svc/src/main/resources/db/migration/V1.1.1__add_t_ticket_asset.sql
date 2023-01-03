create table t_ticket_asset
(
    id            serial primary key,
    ref_ticket_id varchar(37),
    ref_agent_id  varchar(37),

    paths         text[]       not null,

    created_at    timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at    timestamp(0)
)