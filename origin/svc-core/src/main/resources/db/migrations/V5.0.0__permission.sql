create table t_permission
(
    id         varchar(37) primary key,
    name       text         not null,

    created_at timestamp(0) not null default current_timestamp,
    created_by varchar(100) not null,
    updated_at timestamp(0),
    updated_by varchar(100)
);

create table t_role_permission
(
    id            varchar(37) primary key,
    role_id       varchar(37) not null,
    permission_id varchar(37) not null,

    constraint fk_role_id foreign key (role_id) references t_role (id),
    constraint fk_permission_id foreign key (permission_id) references t_permission (id)
)