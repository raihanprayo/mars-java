drop view v_leader_board_fragment;

create or replace view v_leader_board_fragment as
select wl.id              as id,
       wl.solution        as solution_id,
       tc.id              as ticket_id,
       ws.id              as workspace_id,
       ag.id              as agent_id,

       iss.name           as issue,
       iss.product        as product,

       wl.tc_take_status  as take_status,
       wl.tc_close_status as close_status,

       (case
            when wl.tc_close_status is null
                then (interval '0 second')
            else
                sum(wl.updated_at - wl.created_at)
           end)           as action_duration,

       tc.created_at      as ticket_created_at,
       tc.updated_at      as ticket_updated_at
from t_agent_worklog wl
         join t_agent_workspace ws on ws.id = wl.ref_workspace_id
         join t_agent ag on ag.id = ws.ref_agent_id
         join t_ticket tc on tc.id = ws.ref_ticket_id
         join t_issue iss on iss.id = tc.ref_issue_id
group by tc.id,
         tc.created_at,
         tc.updated_at,
         wl.created_at,
         wl.updated_at,
         ag.id,
         ws.id,
         wl.id,
         iss.product,
         iss.name,
         wl.tc_take_status,
         wl.tc_close_status;