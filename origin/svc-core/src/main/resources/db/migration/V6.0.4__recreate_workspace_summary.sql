create or replace view v_worklog_summary
as
select wl.id               as id,
       wl.ref_workspace_id as workspace_id,
       ws.ref_ticket_id    as ticket_id,
       ag.id               as agent_id,
       ag.ref_user_id      as user_id,

       ws.status           as ag_status,

       wl.sol_id           as sol_id,
       wl.sol_name         as sol_name,
       wl.sol_desc         as sol_desc,

       wl.tc_take_status   as tc_take_status,
       wl.tc_close_status  as tc_close_status,

       ws.created_at       as ws_created_at,
       ws.updated_at       as ws_updated_at,

       wl.created_by       as created_by,
       wl.created_at       as created_at,
       wl.updated_by       as updated_by,
       wl.updated_at       as updated_at

from t_agent_worklog wl
         join t_agent_workspace ws on ws.id = wl.ref_workspace_id
         join t_agent ag on ag.id = ws.ref_agent_id