create sequence reported_post_seq;

alter sequence reported_post_seq owner to postgres;

create table reported_categories
(
    id   bigint not null
        primary key,
    name text
);

alter table reported_categories
    owner to postgres;

create table reported_post
(
    id          bigint default nextval('reported_post_seq'::regclass) not null
        primary key,
    comment     text,
    reporter_id uuid,
    post_id     uuid,
    created_at  date,
    category    bigint
        constraint category
            references reported_categories
);

alter table reported_post
    owner to postgres;

create table log
(
    message    text      not null,
    id         serial
        constraint log_pk
            primary key,
    level      integer   not null,
    created_at timestamp not null
);

alter table log
    owner to postgres;

