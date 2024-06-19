drop view v_ticket_summary;
create view v_ticket_summary as
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

       (wip.id IS NOT NULL)                as wip,
       wip.id                              as wip_id,
       wip.status                          as wip_status,
       wip.user_id                         as wip_by,

       array_agg(wl.id order by wl.id)     as wls,

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
         join t_agent_workspace wip on wip.ref_ticket_id = tc.id and wip.status = 'PROGRESS'
         join t_agent_workspace ws on ws.ref_ticket_id = tc.id
         join t_agent_worklog wl on wl.ref_workspace_id = ws.id

group by tc.id, wip.id, wip.status;

