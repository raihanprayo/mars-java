drop view v_worklog_summary;

alter table t_agent_workspace
    add column created_by varchar(100) not null default 'system',
    add column updated_by varchar(100);

update t_agent_workspace as ws
set created_by = usr.nik,
    updated_by = usr.nik
from t_agent ag
         join t_user usr on usr.id = ag.ref_user_id
where ag.id = ws.ref_agent_id;

alter table t_agent_worklog
    add column sol_id     bigint,
    add column sol_name   text,
    add column sol_desc   text,
    add column created_by varchar(100) not null default 'system',
    add column updated_by varchar(100);

update t_agent_worklog as wl
set created_by = usr.nik,
    updated_by = usr.nik
from t_agent_workspace ws
         join t_agent ag on ag.id = ws.ref_agent_id
         join t_user usr on usr.id = ag.ref_user_id
where ws.id = wl.ref_workspace_id;

update t_agent_worklog as wl
set sol_id   = sol.id,
    sol_name = sol.name,
    sol_desc = sol.description
from t_solution as sol
where sol.name = wl.solution;