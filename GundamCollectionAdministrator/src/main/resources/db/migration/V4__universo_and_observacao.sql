create table if not exists universo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome varchar(60) not null unique,
    sigla varchar(8) not null unique,
    principais_series varchar(400),
    descricao varchar(800)
);

alter table gundam_kits add column universo_id bigint references universo(id);
alter table gundam_kits add column observacao varchar(2000);




