create table t_app_config
(
    id          int4 primary key,
    name        varchar(100) unique not null,
    type        varchar(255),
    class_type  varchar(255),
    value       text,
    description text,

    created_at  timestamp(0)        not null default CURRENT_TIMESTAMP,
    created_by  varchar(255)        not null,
    updated_at  timestamp(0)                 default CURRENT_TIMESTAMP,
    updated_by  varchar(255)
);