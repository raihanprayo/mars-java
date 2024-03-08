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
       ag.ref_user_id                      as wip_by,

       tc.closed_at                        as closed_at,

       tc.created_at                       as created_at,
       tc.created_by                       as created_by,
       tc.updated_at                       as updated_at,
       tc.updated_by                       as updated_by

from t_ticket as tc
         left join t_agent_workspace ag_ws on ag_ws.ref_ticket_id = tc.id and ag_ws.status = 'PROGRESS'
         left join t_agent ag on ag.id = ag_ws.ref_agent_id

group by tc.id, ag_ws.id, ag_ws.status, ag.ref_user_id;