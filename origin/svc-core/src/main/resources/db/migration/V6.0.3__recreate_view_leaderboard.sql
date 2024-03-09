create or replace view v_leader_board_fragment as
select wl.id              as id,
--        wl.solution        as solution_id,
       tc.id              as ticket_id,
       ws.id              as workspace_id,
       ag.id              as agent_id,

--        tc.iss_name        as issue,
--        tc.iss_product     as product,

       wl.tc_take_status  as take_status,
       wl.tc_close_status as close_status,

       tc.iss_id,
       tc.iss_product,
       tc.iss_name,
       tc.iss_desc,
       tc.iss_score,
       wl.sol_id,
       wl.sol_name,
       wl.sol_desc,

       (case
            when wl.tc_close_status is null
                then (interval '0 second')
            else
                sum(wl.updated_at - wl.created_at)
           end)           as action_duration,

       tc.created_at      as ticket_created_at,
       tc.updated_at      as ticket_updated_at,

       wl.created_by      as created_by,
       wl.created_at      as created_at,
       wl.updated_by      as updated_by,
       wl.updated_at      as updated_at
from t_agent_worklog wl
         join t_agent_workspace ws on ws.id = wl.ref_workspace_id
         join t_agent ag on ag.id = ws.ref_agent_id
         join t_ticket tc on tc.id = ws.ref_ticket_id
group by tc.id,
         tc.created_at,
         tc.updated_at,
         wl.created_at,
         wl.updated_at,
         ag.id,
         ws.id,
         wl.id,
         wl.tc_take_status,
         wl.tc_close_status;