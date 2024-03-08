create table t_app_config
(
    id          int4 primary key,
    name        varchar(100) unique not null,
    title       varchar(100)        not null,
    type        varchar(255),
    class_type  varchar(255),
    value       text,
    description text,

    created_at  timestamp(0)        not null default CURRENT_TIMESTAMP,
    created_by  varchar(255)        not null,
    updated_at  timestamp(0)                 default CURRENT_TIMESTAMP,
    updated_by  varchar(255)
);
create table t_sto
(
    id    serial primary key,
    witel varchar(20)       not null,
    datel varchar(50)       not null,
    alias varchar(5) unique not null,
    name  varchar(50)
);

create table t_issue
(
    id           serial primary key,
    name         varchar(255) not null,
    display_name varchar(50),
    product      varchar(50)  not null,

    description  text,

    created_at   timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by   varchar(255) not null,
    updated_at   timestamp(0)          default CURRENT_TIMESTAMP,
    updated_by   varchar(255)
);

create table t_issue_param
(
    id           serial primary key,
    type         varchar(20)  not null,
    required     bool         not null,
    display_name varchar(30),

    ref_issue_id bigint       not null,

    created_at   timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by   varchar(100) not null,
    updated_at   timestamp(0),
    updated_by   varchar(100),

    constraint fk_ref_issue_id foreign key (ref_issue_id) references t_issue (id)
);