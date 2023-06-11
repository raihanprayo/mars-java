create table t_app_config_category
(
    id         varchar(100) primary key,
    created_at timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at timestamp(0)
);

create table t_app_config
(
    id              serial primary key,
    ref_category_id varchar(100)        not null,

    name            varchar(100) unique not null,
    title           varchar(100),

    type            varchar(15)         not null,
    class_type      text                not null,
    value           text,

    description     text,

    created_at      timestamp(0)        not null default CURRENT_TIMESTAMP,
    created_by      varchar(100)        not null,
    updated_at      timestamp(0),
    updated_by      varchar(100)
);