drop view v_leader_board_fragment;

alter table t_agent_worklog
    alter column solution type text;

UPDATE t_agent_worklog
-- SET taw.solution = (select distinct ts.name
--                     from t_solution ts
--                     where ts.id = taw.solution::bigint)
SET solution = ts.name
FROM t_solution ts
    where solution = ts.id::text;