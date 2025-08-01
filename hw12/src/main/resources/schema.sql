create table authors (
    id bigserial,
    full_name varchar(255),
    primary key (id)
);

create table genres (
    id bigserial,
    name varchar(255),
    primary key (id)
);

create table books (
    id bigserial,
    title varchar(255),
    author_id bigint references authors (id) on delete cascade,
    genre_id bigint references genres (id) on delete cascade,
    primary key (id)
);

create table comments (
    id bigserial,
    text varchar(255),
    book_id bigint references books (id) on delete cascade,
    primary key (id)
);

create table roles (
    id serial,
    name varchar(32),
    primary key (id)
);

create table users (
    id bigserial,
    username varchar(64) not null unique,
    password varchar(128),
    primary key (id)
);

create table users_roles (
    user_id bigserial references users (id),
    role_id serial references roles (id),
    primary key (user_id, role_id)
);