create table t_role
(
    id         varchar(37) primary key,
    name       varchar(255) not null,

    created_at timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by varchar(255) not null,
    updated_at timestamp(0)          default CURRENT_TIMESTAMP,
    updated_by varchar(255)
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