-- Worklog Summary
create or replace view v_worklog_summary
as
select wl.id               as id,
       wl.ref_workspace_id as workspace_id,
       ws.ref_ticket_id    as ticket_id,
--        ag.id               as agent_id,
       ws.user_id          as user_id,

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
         join t_agent_workspace ws on ws.id = wl.ref_workspace_id;


-- Leaderboard Fragment
create or replace view v_leader_board_fragment as
select wl.id                                 as id,
       tc.id                                 as ticket_id,
       ws.id                                 as workspace_id,
       ws.user_id                            as user_id,

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
         join t_ticket tc on tc.id = ws.ref_ticket_id and tc.deleted is false
group by tc.id,
         ws.id,
         wl.id,
         wl.created_at,
         wl.updated_at;


-- Ticket Summary
create or replace view v_ticket_summary as
select tc.id                               as id,
       tc.no                               as no,
       tc.witel                            as witel,
       tc.sto                              as sto,
       tc.status                           as status,
       tc.source                           as source,
       (tc.gaul > 0)                       as is_gaul,
       tc.gaul                             as gaul,

       tc.incident_no                      as incident_no,
       tc.service_no                       as service_no,

       tc.sender_name                      as sender_name,
       tc.sender_id                        as sender_id,

       tc.note                             as note,

       tc.iss_product                      as product,
       tc.iss_id,
       tc.iss_name,
       tc.iss_desc,
       tc.iss_score,

       (select count("id")
        from t_agent_workspace as agent
        where agent.ref_ticket_id = tc.id) as agent_count,

       (ag_ws.id IS NOT NULL)              as wip,
       ag_ws.id                            as wip_id,
       ag_ws.status                        as wip_status,
       ag_ws.user_id                       as wip_by,

       (case
            when tc.status = 'CLOSED'
                then tc.closed_at - tc.created_at
            else
                        current_timestamp - tc.created_at
           end)::interval                  as age,

       tc.closed_at                        as closed_at,
       tc.deleted                          as deleted,
       tc.deleted_at                       as deleted_at,

       tc.created_at                       as created_at,
       tc.created_by                       as created_by,
       tc.updated_at                       as updated_at,
       tc.updated_by                       as updated_by

from t_ticket as tc
         left join t_agent_workspace ag_ws on ag_ws.ref_ticket_id = tc.id and ag_ws.status = 'PROGRESS'
--          left join t_agent ag on ag.id = ag_ws.ref_agent_id

group by tc.id, ag_ws.id, ag_ws.status;