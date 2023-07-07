alter table t_issue
    add column deleted bool not null default false;