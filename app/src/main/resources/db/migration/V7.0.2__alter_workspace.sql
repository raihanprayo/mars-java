drop view v_leader_board_fragment;
drop view v_ticket_summary;
drop view v_worklog_summary;

alter table t_agent_workspace
    add column user_id varchar(37),
    add constraint fk_user_id foreign key (user_id) references t_user (id);

--     drop column ref_agent_id,
--     drop constraint fk_agent_id;

update t_agent_workspace taw
set user_id = (select ref_user_id
               from t_agent ag
               where ag.id = taw.ref_agent_id);

alter table t_agent_workspace
    alter column user_id set not null,
    drop constraint fk_agent_id,
    drop column ref_agent_id,
    drop constraint fk_ticket_id,
    add constraint fk_ticket_id foreign key (ref_ticket_id) references t_ticket (id) on delete cascade;

alter table t_agent_worklog
    drop constraint fk_workspace_id,
    add constraint fk_workspace_id foreign key (ref_workspace_id) references t_agent_workspace (id) on delete cascade;


alter table t_log_ticket
    add column user_id varchar(37);

update t_log_ticket tlt
set user_id = (select ref_user_id
               from t_agent ta
               where ta.id = tlt.ref_agent_id)
where tlt.ref_agent_id is not null;

alter table t_log_ticket
    drop column ref_agent_id,
    alter column ref_ticket_no set data type varchar(50),
    add constraint fk_ticket_no foreign key (ref_ticket_no) references t_ticket (no) on delete cascade;