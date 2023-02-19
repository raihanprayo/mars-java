create table t_agent
(
    id          varchar(37) primary key,
    nik         varchar(100) unique not null,
    tg_id       bigint unique       not null,

    ref_user_id varchar(37)         not null,
--     status             varchar(20)  not null,
--
--     tc_take_status     varchar(20),
--     tc_close_status    varchar(20),
--
--     ref_ticket_id      varchar(37)  not null,
--     ref_user_id        varchar(37)  not null,
--
--     description        text,
--     reopen_description text,

    created_at  timestamp(0)        not null default CURRENT_TIMESTAMP,
    created_by  varchar(255)        not null,
    updated_at  timestamp(0)                 default CURRENT_TIMESTAMP,
    updated_by  varchar(255),

    constraint fk_ref_ticket_id foreign key (ref_ticket_id) references t_ticket (id),
    constraint fk_ref_user_id foreign key (ref_user_id) references t_user (id)
);

create table t_agent_workspace
(
    id            serial primary key,
    status        varchar(15)  not null,

    ref_agent_id  varchar(37)  not null,
    ref_ticket_id varchar(37)  not null,

    created_at    timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at    timestamp(0),

    constraint fk_ticket_id foreign key (ref_ticket_id) references t_ticket (id),
    constraint fk_agent_id foreign key (ref_agent_id) references t_agent (id)
);

create table t_agent_worklog
(
    id               serial primary key,
    ref_workspace_id bigint       not null,

    tc_take_status   varchar(20),
    tc_close_status  varchar(20),

    solution         bigint,
    message          text,
    reopen_message   text,

    created_at       timestamp(0) not null default CURRENT_TIMESTAMP,
    updated_at       timestamp(0),

    constraint fk_workspace_id foreign key (ref_workspace_id) references t_agent_workspace (id)
);