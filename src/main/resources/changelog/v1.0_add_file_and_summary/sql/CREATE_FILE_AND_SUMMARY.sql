create table if not exists file
(
    id           varchar(50) primary key,
    url          varchar(255) not null,
    size         integer      not null,
    content_type varchar(100) not null,
    content      bytea        not null
);

create table if not exists summary
(
    files_count integer not null,
    files_size  bigint  not null
);

CREATE SEQUENCE if not exists file_sequence START 101;


