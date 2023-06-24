create table t_sto
(
    id         serial primary key,
    witel      varchar(20)       not null,
    datel      varchar(50)       not null,
    alias      varchar(5) unique not null,
    name       varchar(50),

    created_at timestamp(0)      not null default CURRENT_TIMESTAMP,
    created_by varchar(255)      not null,
    updated_at timestamp(0)               default CURRENT_TIMESTAMP,
    updated_by varchar(255)
);

create table t_issue
(
    id           serial primary key,
    code         varchar(255) not null,
    display_name varchar(50),
    product      varchar(30)  not null,

    description  text,

    created_at   timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by   varchar(255) not null,
    updated_at   timestamp(0)          default CURRENT_TIMESTAMP,
    updated_by   varchar(255)
);