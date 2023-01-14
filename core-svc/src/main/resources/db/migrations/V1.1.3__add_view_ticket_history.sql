create or replace view v_ticket_history as
select tc.id,
       tc.no

from t_ticket as tc
group by tc.id
