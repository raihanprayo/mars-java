create table t_group
(
    id              varchar(37) primary key,
    name            varchar(255) unique not null,

    parent_group_id varchar(37),

    created_at      timestamp(0)        not null default CURRENT_TIMESTAMP,
    created_by      varchar(255)        not null,
    updated_at      timestamp(0)                 default CURRENT_TIMESTAMP,
    updated_by      varchar(255),

    constraint fk_parent_group_id foreign key (parent_group_id) references t_group (id)
);
create table t_group_setting
(
    id                  serial primary key,
    ref_group_id        varchar(37) unique not null,
    ref_default_role_id varchar(37),

    can_login           bool                        default false,

    created_at          timestamp(0)       not null default CURRENT_TIMESTAMP,
    created_by          varchar(255)       not null,
    updated_at          timestamp(0)                default CURRENT_TIMESTAMP,
    updated_by          varchar(255),

    constraint fk_ref_group_id foreign key (ref_group_id) references t_group (id)
);


create table t_role
(
    id           varchar(37) primary key,
    name         varchar(255) not null,
    "order"      int4         not null,

    ref_group_id varchar(37),

    created_at   timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by   varchar(255) not null,
    updated_at   timestamp(0)          default CURRENT_TIMESTAMP,
    updated_by   varchar(255),

    constraint fk_ref_group_id foreign key (ref_group_id) references t_group (id)
);

alter table t_group_setting
    add constraint fk_ref_default_role_id foreign key (ref_default_role_id) references t_role (id);


create table t_user
(
    id           varchar(37) primary key,
    name         varchar(255)       not null,
    nik          varchar(50) unique not null,
    phone        varchar(50)        not null,
    tg_id        bigint unique      not null,

    active       bool                        default false,

    ref_group_id varchar(37),

    created_at   timestamp(0)       not null default CURRENT_TIMESTAMP,
    created_by   varchar(255)       not null,
    updated_at   timestamp(0)                default CURRENT_TIMESTAMP,
    updated_by   varchar(255)
);
create table t_user_credential
(
    id          serial primary key,
    ref_user_id varchar(37)  not null,

    username    varchar(100) unique,
    email       varchar(100) unique,
    password    text,

    created_at  timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by  varchar(255) not null,
    updated_at  timestamp(0)          default CURRENT_TIMESTAMP,
    updated_by  varchar(255),

    constraint fk_ref_user_id foreign key (ref_user_id) references t_user (id)
);
create table t_user_setting
(
    id          serial primary key,
    ref_user_id varchar(37)  not null,

    language    varchar(15),

    created_at  timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by  varchar(255) not null,
    updated_at  timestamp(0)          default CURRENT_TIMESTAMP,
    updated_by  varchar(255),

    constraint fk_ref_user_id foreign key (ref_user_id) references t_user (id)
);

create table t_roles
(
    id          serial primary key,
    ref_user_id varchar(37)  not null,
    ref_role_id varchar(37)  not null,

    created_at  timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at  timestamp(0)          default CURRENT_TIMESTAMP,

    constraint fk_ref_user_id foreign key (ref_user_id) references t_user (id),
    constraint fk_ref_role_id foreign key (ref_role_id) references t_role (id)
);