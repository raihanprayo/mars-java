drop view v_ticket_summary;
create or replace view v_ticket_summary as
select tc.id                                           as id,
       tc.no                                           as no,
       tc.witel                                        as witel,
       tc.sto                                          as sto,
       tc.status                                       as status,
       tc.source                                       as source,
       (tc.gaul > 0)                                   as is_gaul,
       tc.gaul                                         as gaul,

       tc.incident_no                                  as incident_no,
       tc.service_no                                   as service_no,

       tc.sender_name                                  as sender_name,
       tc.sender_id                                    as sender_id,

       tc.note                                         as note,
       tc.ref_issue_id                                 as ref_issue_id,

       issue.product                                   as product,

       (select count("id")
        from t_agent_workspace as agent
        where agent.ref_ticket_id = tc.id)             as agent_count,

       (ag_ws.id IS NOT NULL)                          as wip,
       ag_ws.id                                        as wip_id,
       ag_ws.status                                    as wip_status,
       ag.ref_user_id                                  as wip_by,

       (case
            when tc.status = 'CLOSED'
                then tc.updated_at - tc.created_at
            else current_timestamp - tc.created_at
           end)                                        as age,

       (select distinct on (lc.ref_ticket_no) lc.created_at
        from t_log_ticket as lc
        where lc.ref_ticket_no = tc.no
        order by lc.ref_ticket_no, lc.created_at desc) as age_action,

       (select distinct on (lc.ref_ticket_no) lc.created_at
        from t_log_ticket as lc
        where lc.ref_ticket_no = tc.no
          and lc.prev_status = 'OPEN'
          and lc.curr_status = 'PROGRESS'
        order by lc.ref_ticket_no, lc.created_at desc) as age_response,

       tc.created_at                                   as created_at,
       tc.created_by                                   as created_by,
       tc.updated_at                                   as updated_at,
       tc.updated_by                                   as updated_by

from t_ticket as tc
         join t_issue issue on tc.ref_issue_id = issue.id
         left join t_agent_workspace ag_ws on ag_ws.ref_ticket_id = tc.id and ag_ws.status = 'PROGRESS'
         left join t_agent ag on ag.id = ag_ws.ref_agent_id

group by tc.id, issue.product, ag_ws.id, ag_ws.status, ag.ref_user_id;