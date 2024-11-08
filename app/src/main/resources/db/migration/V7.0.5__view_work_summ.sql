update t_solution
set showable = true
where name = 'Force Close';

drop view if exists v_leader_board_fragment;

create or replace view leaderboard_frg as
with tmp_drt as (select wl.id              as id,
                        ws.id              as wsi,
                        tc.id              as tci,
                        (case
                             when wl.tc_close_status is null
                                 then interval '0 second'
                             else
                                 wl.updated_at - wl.created_at
                            end)::interval as act,
                        (case
                             when wl.tc_take_status in ('OPEN', 'DISPATCH')
                                 then wl.created_at - (select max(lt.created_at)
                                                       from t_log_ticket lt
                                                       where lt.ref_ticket_no = tc.no
                                                         and lt.created_at < wl.created_at)
                             else
                                 interval '0 second'
                            end)::interval as resp

                 from t_agent_worklog wl
                          join t_agent_workspace ws on ws.id = wl.ref_workspace_id
                          join t_ticket tc on tc.id = ws.ref_ticket_id)
select wl.id              as id,
       tc.id              as tc_id,
       tc.no              as tc_no,
       ws.id              as ws_id,

       (case
            when wl.id = (select twl.id
                          from t_agent_worklog twl
                                   join t_agent_workspace tws on tws.id = twl.ref_workspace_id
                                   join t_ticket ttc on ttc.id = tws.ref_ticket_id
                          where ttc.id = tc.id
                          order by twl.id desc
                          limit 1)
                then true
            else false
           end)           as last_tc_wl,

       (case
            when wl.id = (select twl.id
                          from t_agent_worklog twl
                                   join t_agent_workspace tws on tws.id = twl.ref_workspace_id
                                   join t_ticket ttc on ttc.id = tws.ref_ticket_id
                          where ttc.id = tc.id
                            and tws.user_id = ws.user_id
                          order by twl.id desc
                          limit 1)
                then true
            else false
           end)           as last_ag_wl,

       ag.id              as ag_id,
       ag.nik             as ag_nik,
       ag.name            as ag_name,

       rq.id              as rq_id,
       rq.nik             as rq_nik,
       rq.name            as rq_name,

       wl.tc_take_status  as st_take,
       wl.tc_close_status as st_end,

       tc.iss_score       as score,
       drt.act            as drt_act,
       drt.resp           as drt_response,

       wl.sol_id,
       wl.sol_name,

       tc.iss_id,
       tc.iss_name,
       tc.iss_product,

       tc.created_at      as tc_created_at,

       wl.created_by,
       wl.created_at,
       wl.updated_by,
       wl.updated_at

-- from t_agent_worklog wl
--          join t_agent_workspace ws on ws.id = wl.ref_workspace_id
--          join t_ticket tc on tc.id = ws.ref_ticket_id
from t_ticket tc
         join t_agent_workspace ws on ws.ref_ticket_id = tc.id
         join t_agent_worklog wl on wl.ref_workspace_id = ws.id
         left join t_user ag on ag.id = ws.user_id
         left join t_user rq on rq.tg_id = tc.sender_id
         join tmp_drt drt on drt.id = wl.id and drt.wsi = ws.id and drt.tci = tc.id
where tc.deleted = false
  and tc.status = 'CLOSED'
-- order by tc.created_at, wl.id
order by wl.id