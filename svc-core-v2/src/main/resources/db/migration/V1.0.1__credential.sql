create table t_role
(
    id         varchar(37) primary key,
    name       varchar(100) unique not null,

    created_at timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_at timestamp(0)
);

create table t_account
(
    id         varchar(37) primary key,
    username   varchar(30) unique not null,
    name       varchar(100)       not null,

    active     bool               not null default false,

    created_by varchar(100)       not null,
    created_at timestamp(0)       not null default CURRENT_TIMESTAMP,
    updated_by varchar(100),
    updated_at timestamp(0)
);

create table t_account_role
(
    id         serial primary key,
    account_id varchar(37) not null,
    role_id    bigint      not null,
    constraint fk_account_id foreign key (account_id) references t_account (id),
    constraint fk_role_id foreign key (role_id) references t_role (id)
);