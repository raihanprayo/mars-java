create table t_asset
(
    id         serial primary key,
    path       text         not null,

    created_at timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at timestamp(0)          default CURRENT_TIMESTAMP
);

create table t_ticket_asset
(
    id            serial primary key,
    ref_ticket_id varchar(37) not null,
    ref_asset_id  bigint      not null
);