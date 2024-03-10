create or replace view v_leader_board_fragment as
select wl.id                                 as id,
       tc.id                                 as ticket_id,
       ws.id                                 as workspace_id,

       wl.tc_take_status                     as take_status,
       wl.tc_close_status                    as close_status,

--  Issue
       tc.iss_id,
       tc.iss_product,
       tc.iss_name,
       tc.iss_desc,
       tc.iss_score,
--  Solution
       wl.sol_id,
       wl.sol_name,
       wl.sol_desc,

       (case
            when wl.tc_close_status is null
                then null
            else
                sum(wl.updated_at - wl.created_at)
           end)                              as drt_action,
       (case
            when wl.tc_take_status in ('OPEN', 'DISPATCH')
                then wl.created_at - (select max(lt.created_at)
                                      from t_log_ticket lt
                                      where lt.ref_ticket_no = tc.no
                                        and lt.created_at < wl.created_at)
           end)                              as drt_response,
       (select max(lt.created_at)
        from t_log_ticket lt
        where lt.ref_ticket_no = tc.no
          and lt.created_at < wl.created_at) as last_log_at,

       wl.created_by                         as created_by,
       wl.created_at                         as created_at,
       wl.updated_by                         as updated_by,
       wl.updated_at                         as updated_at
from t_agent_worklog wl
         join t_agent_workspace ws on ws.id = wl.ref_workspace_id
         join t_agent ag on ag.id = ws.ref_agent_id
         join t_ticket tc on tc.id = ws.ref_ticket_id and tc.deleted is false
group by tc.id,
         ws.id,
         wl.id,
         wl.created_at,
         wl.updated_at;