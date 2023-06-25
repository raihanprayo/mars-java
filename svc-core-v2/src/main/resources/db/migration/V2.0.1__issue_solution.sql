create table t_solution
(
    id          serial primary key,
    name        varchar(100) unique not null,
    description text,
    product     varchar(15),

    created_at  timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_at  timestamp(0)                 default CURRENT_TIMESTAMP
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