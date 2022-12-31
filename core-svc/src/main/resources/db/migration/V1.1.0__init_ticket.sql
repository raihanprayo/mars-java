create table t_issue
(
    id          varchar(37) primary key,
    name        varchar(255) not null,
    product     varchar(50)  not null,

    description text,

    created_at  timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by  varchar(255) not null,
    updated_at  timestamp(0)          default CURRENT_TIMESTAMP,
    updated_by  varchar(255)
);

create table t_ticket
(
    id           varchar(37) primary key,
    no           varchar(100) unique not null,
    witel        varchar(20)         not null,
    sto          varchar(20)         not null,
    status       varchar(20)         not null,
    source       varchar(20)         not null,
    gaul         int4                not null default 0::int4,

    incident_no  varchar(100)        not null,
    service_no   varchar(100)        not null,

    sender_name  varchar(255)        not null,
    sender_id    bigint              not null,

    note         text,

    ref_issue_id varchar(37)         not null,

    created_at   timestamp(0)        not null default CURRENT_TIMESTAMP,
    created_by   varchar(255)        not null,
    updated_at   timestamp(0)                 default CURRENT_TIMESTAMP,
    updated_by   varchar(255),

    constraint fk_ref_issue_id foreign key (ref_issue_id) references t_issue (id)
);

create table t_ticket_agent
(
    id                 varchar(37) primary key,
    status             varchar(20)  not null,
    tc_close_status    varchar(20),

    ref_ticket_id      varchar(37)  not null,
    ref_user_id        varchar(37)  not null,

    description        text,
    reopen_description text,

    created_at         timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by         varchar(255) not null,
    updated_at         timestamp(0)          default CURRENT_TIMESTAMP,
    updated_by         varchar(255),

    constraint fk_ref_ticket_id foreign key (ref_ticket_id) references t_ticket (id),
    constraint fk_ref_user_id foreign key (ref_user_id) references t_user (id)
);