alter table t_solution
    add column created_by  varchar(100) not null default 'system',
    add column updated_by  varchar(100),
    add column showable    bool         not null default true,
    add column delete_able bool         not null default true;

insert into t_solution (name, delete_able, showable)
values ('Force Close', false, false);