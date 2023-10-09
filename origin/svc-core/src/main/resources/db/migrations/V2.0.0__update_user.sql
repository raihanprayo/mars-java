create table t_credential
(
    id             serial primary key,
    user_id        varchar(37)  not null,

    algo           varchar(30)  not null,
    secret         text,
    hash_iteration int4,

    password       text,
    priority       int4         not null,

    created_at     timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at     timestamp(0),
    constraint fk_account_id foreign key (user_id) references t_user (id)
);

create table t_user_event
(
    id         varchar(37) primary key,
    type       varchar(30)  not null,
    details    jsonb,

    created_at timestamp(3) not null default CURRENT_TIMESTAMP,
    created_by varchar(100) not null
);

alter table t_role
    drop column "order";

insert into t_credential (user_id, algo, password, priority)
        (select id, 'bcrypt', password, 10 from t_user);

alter table t_user
    drop column password,
    drop column ref_group_id;

drop table t_group_setting cascade;
drop table t_group cascade;
