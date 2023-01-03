create or replace view v_ticket_summary as
select tc.id                               as id,
       tc.no                               as no,
       tc.witel                            as witel,
       tc.sto                              as sto,
       tc.status                           as status,
       (tc.gaul > 0)                       as is_gaul,
       tc.gaul                             as gaul,

       tc.incident_no                      as incident_no,
       tc.service_no                       as service_no,

       tc.sender_name                      as sender_name,
       tc.sender_id                        as sender_id,

       tc.note                             as note,
       tc.ref_issue_id                     as ref_issue_id,

       issue.product                       as product,

       (select count("id")
        from t_ticket_agent as agent
        where agent.ref_ticket_id = tc.id) as agent_count,

       (select count("id") > 0
        from t_ticket_agent as agent
        where agent.ref_ticket_id = tc.id
          and agent.status = 'PROGRESS')   as wip,

       (select agent.ref_user_id
        from t_ticket_agent as agent
        where agent.ref_ticket_id = tc.id
          and agent.status = 'PROGRESS')   as wip_by,

       asset.paths                         as assets,

       tc.created_at                       as created_at,
       tc.created_by                       as created_by,
       tc.updated_at                       as updated_at,
       tc.updated_by                       as updated_by
from t_ticket as tc
         join t_issue issue on tc.ref_issue_id = issue.id
         left join t_ticket_asset asset on asset.ref_ticket_id = tc.id
group by tc.id, issue.product, asset.paths