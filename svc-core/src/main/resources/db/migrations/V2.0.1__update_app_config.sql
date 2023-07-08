create table t_config_tag
(
    id         serial primary key,
    name       varchar(100) unique not null,
    created_at timestamp(0)        not null default CURRENT_TIMESTAMP
);

create table t_config
(
    id          varchar(100) primary key,
    value       text,
    type        varchar(50)  not null,
    tag_id      bigint,
    description text,

    created_by  varchar(100) not null,
    created_at  timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_by  varchar(100),
    updated_at  timestamp(0),
    constraint fk_tag_id foreign key (tag_id) references t_config_tag (id)
);