create table t_ticket
(
    id            varchar(37) primary key,
    no            varchar(15) unique not null,
    status        varchar(15)        not null,
    source        varchar(15)        not null,
    ga_ul         int4               not null,

    incident_no   varchar(30),
    service_no    varchar(30),

    issue_product varchar(10)        not null,
    issue_name    varchar(10)        not null,

    witel         varchar(10)        not null,
    sto           varchar(10),
    sender_name   varchar(100)       not null,
    sender_tg     bigint             not null,

    created_at    timestamp(0)       not null default CURRENT_TIMESTAMP,
    created_by    varchar(255)       not null,
    updated_at    timestamp(0)                default CURRENT_TIMESTAMP,
    updated_by    varchar(255)
);

create table t_ticket_history
(
    id         varchar(37)  not null,
    ticket_no  varchar(15)  not null,

    created_at timestamp(0) not null default CURRENT_TIMESTAMP,
    created_by varchar(255) not null
);