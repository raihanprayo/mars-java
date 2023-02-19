
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
create table t_solution
(
    id          serial primary key,
    name        varchar(100) unique not null,
    description text,
    product     varchar(15),

    created_at  timestamp(0)        not null default CURRENT_TIMESTAMP,
    updated_at  timestamp(0)                 default CURRENT_TIMESTAMP
);

create table t_ticket
(
    id                     varchar(37) primary key,
    no                     varchar(50) unique not null,
    witel                  varchar(15)        not null,
    sto                    varchar(5),
    status                 varchar(15)        not null,
    source                 varchar(15)        not null,
    gaul                   int4               not null default 0::int4,

    incident_no            varchar(100),
    service_no             varchar(100)       not null,

    sender_name            varchar(255)       not null,
    sender_id              bigint             not null,

    note                   text,

    con_message_id         bigint,
    con_pending_message_id bigint,
    ref_issue_id           bigint             not null,

    created_at             timestamp(0)       not null default CURRENT_TIMESTAMP,
    created_by             varchar(255)       not null,
    updated_at             timestamp(0)                default CURRENT_TIMESTAMP,
    updated_by             varchar(255),

    constraint fk_ref_issue_id foreign key (ref_issue_id) references t_issue (id)
);

create table t_log_ticket
(
    id            serial primary key,
    ref_ticket_no varchar(37)  not null,

    message       varchar(100),

    prev_status   varchar(15),
    curr_status   varchar(15),

    ref_agent_id  varchar(37),

    created_at    timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by    varchar(100) not null
);

create table t_ticket_confirm
(
    id           bigint primary key,
    no           varchar(50),
    status       varchar(30)  not null,
    ttl          int4                  default 30,

    ref_issue_id bigint,

    created_at   timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by   varchar(100) not null
);

create table t_ticket_asset
(
    id            serial primary key,
    ref_ticket_id varchar(37),
    ref_agent_id  varchar(37),

    paths         text[]       not null,

    created_at    timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at    timestamp(0)
);