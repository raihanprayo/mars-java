drop view v_ticket_summary;
drop view v_leader_board_fragment;

alter table t_ticket
    add column closed_at   timestamp(0),
    add column deleted     bool           not null default false,
    add column deleted_at  timestamp(0),
    add column iss_id      bigint,
    add column iss_product varchar(15),
    add column iss_name    text,
    add column iss_desc    text,
    add column iss_score   numeric(20, 2) not null default 0;

alter table t_sto
    alter column alias type varchar(10);

alter table t_ticket
    alter column sto type varchar(10),
    alter column ref_issue_id drop not null,
    drop constraint fk_ref_issue_id;

update t_ticket as tc
set iss_id      = tc.ref_issue_id,
    iss_product = ti.product,
    iss_name    = ti.name,
    iss_score   = ti.score,
    iss_desc    = ti.description,
    closed_at   = ti.updated_at
from t_issue as ti
where ti.id = tc.ref_issue_id;

alter table t_ticket
    drop column ref_issue_id;