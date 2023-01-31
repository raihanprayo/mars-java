alter table t_ticket
    alter column incident_no drop not null;
alter table t_issue
    add column display_name varchar(50);
alter table t_ticket_confirm
    add column issue_id bigint;
alter table t_ticket_confirm
    alter column "no" drop not null;

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
    updated_by   varchar(100)
);