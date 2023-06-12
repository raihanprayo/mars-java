create table sequences_entry
(
    name      varchar(100) primary key,
    execution timestamp(0) not null default current_timestamp
);

create sequence ticket_index
    start 1
    minvalue 0
    maxvalue 999999;

insert into sequences_entry (name)
values ('ticket_index');

-------------------------------------------------------------------------------------------
-- Functions ------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------
create function reset_ticket_index(current timestamp(0)) returns void as
$$
declare
    sq           text         := 'ticket_index';
    sq_exec_time timestamp(0) := (select (execution)
                                  from sequences_entry
                                  where name = sq
                                  limit 1);
begin
    if extract(DAY from sq_exec_time) != extract(DAY from current) then
        alter sequence ticket_index restart 0;
    end if;

    update sequences_entry set execution = current_timestamp where name = sq;
end
$$
    language plpgsql
    volatile;

create function generate_ticket_id() returns text as
$$
declare
    sq text := 'ticket_index';
begin
    perform reset_ticket_index(current_timestamp::timestamp(0));
    return to_char(now(), 'YYMMDD') || lpad(nextval(sq)::text, 6, '0');
end
$$
    language plpgsql
    immutable;

-------------------------------------------------------------------------------------------
-- Table Definitions ----------------------------------------------------------------------
-------------------------------------------------------------------------------------------
create table t_ticket
(
    no               varchar(15) primary key generated always as ( generate_ticket_id() ) STORED,
    incident_no      varchar(100),
    service_no       varchar(100),

    status           varchar(15)  not null default 'OPEN',
    gaul             bool         not null default false,

    src_witel        varchar(15)  not null,
    src_sto          varchar(15),
    src_sender_name  varchar(100) not null,
    src_sender_tg_id bigint       not null,
    src_chat         varchar(10)  not null default 'PRIVATE',

    issue_product    varchar(15)  not null,
    issue_code       varchar(100) not null,

    closed_at        timestamp(0),

    created_at       timestamp(0) not null default current_timestamp,
    created_by       varchar(100) not null,
    updated_at       timestamp(0),
    updated_by       varchar(100)
);