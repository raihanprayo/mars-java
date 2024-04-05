alter table t_sto
    add column created_by varchar(100) not null default 'system',
    add column created_at timestamp(0) not null default current_timestamp,
    add column updated_by varchar(100),
    add column updated_at timestamp(0);