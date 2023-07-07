create or replace function timestamp_to_milis(tm timestamp) RETURNS numeric
RETURN EXTRACT(EPOCH FROM (tm AT TIME ZONE 'UTC')) * 1000;

create or replace view v_leader_board_fragment as
select ws.id                          as id,
       tc.no                          as ref_ticket_no,
       ws.ref_agent_id                as ref_agent_id,
       ag.ref_user_id                 as ref_user_id,
       issue.name                     as issue_name,
       issue.product                  as issue_product,
       (SUM(timestamp_to_milis(wl_avg_respon.created_at) - timestamp_to_milis(tc.created_at)) /
        COUNT(wl_avg_respon))::bigint as avg_respon,
       COUNT(wl_avg_respon)           as avg_respon_total,
       (case
            when (ws.status = 'CLOSED')
                then (timestamp_to_milis(wl_avg_action.updated_at) - timestamp_to_milis(wl_avg_action.created_at)) /
                     COUNT(wl_avg_action)
            else 0.0
           end)::bigint               as avg_action,
       COUNT(wl_avg_action)           as avg_action_total,
       COUNT(wl_td)                   as total_dispatch,
       COUNT(wl_thd)                  as total_handle_dispatch,
       ws.created_at                  as ws_created_at,
       ws.updated_at                  as ws_updated_at,
       tc.created_at                  as tc_created_at,
       tc.updated_at                  as tc_updated_at

from t_agent_workspace ws
         join t_ticket tc on tc.id = ws.ref_ticket_id
         join t_issue issue on issue.id = tc.ref_issue_id
         join t_agent ag on ag.id = ws.ref_agent_id
         left join t_agent_worklog wl_td on wl_td.ref_workspace_id = ws.id and wl_td.tc_close_status = 'DISPATCH'
         left join t_agent_worklog wl_thd on wl_thd.ref_workspace_id = ws.id and wl_thd.tc_take_status = 'DISPATCH'
         left join t_agent_worklog wl_avg_respon
                   on wl_avg_respon.ref_workspace_id = ws.id and wl_avg_respon.tc_take_status in ('OPEN', 'DISPATCH')
         left join t_agent_worklog wl_avg_action
                   on wl_avg_action.ref_workspace_id = ws.id

group by ws.id, ws.ref_agent_id, ws.ref_ticket_id, tc.no, ag.ref_user_id, ws.created_at,
         ws.updated_at, issue.name, issue.product, tc.created_at, tc.updated_at, wl_avg_respon.created_at,
         wl_avg_action.created_at, wl_avg_action.updated_at;

create or replace view v_leader_board as
select ag.ref_user_id                    as id,
       ag.nik                            as nik,
       ag.tg_id                          as tg_id,

       SUM(frg.avg_respon)               as avg_respon,
       SUM(frg.avg_action)               as avg_action,

       COUNT(DISTINCT frg.ref_ticket_no) as total,
       SUM(frg.total_dispatch)           as total_dispatch,
       SUM(frg.total_handle_dispatch)    as total_handle_dispatch

from t_agent ag
         left join v_leader_board_fragment frg on frg.ref_agent_id = ag.id
group by ag.id, frg.ref_ticket_no