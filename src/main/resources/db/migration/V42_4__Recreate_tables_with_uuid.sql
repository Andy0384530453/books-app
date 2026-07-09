-- Recreate all tables: UUID primary keys, float4 prices, all FK constraints
-- Drops old tables that had varchar PKs and numeric prices first

drop table if exists stock_movement cascade;
drop table if exists sale_item cascade;
drop table if exists book_copy cascade;
drop table if exists book_author cascade;
drop table if exists sale cascade;
drop table if exists book cascade;
drop table if exists customer cascade;
drop table if exists author cascade;
drop table if exists category cascade;

create table category
(
    id_category   uuid        not null constraint category_pk primary key,
    category_name varchar(20) not null
);

create table author
(
    id_author  uuid        not null constraint author_pk primary key,
    last_name  varchar     not null,
    first_name varchar     not null
);

create table book
(
    id_book          uuid    not null constraint book_pk primary key,
    title            varchar not null,
    genre            varchar,
    publication_date date,
    purchase_price   float4  not null,
    selling_price    float4  not null,
    id_category      uuid    references category (id_category)
);

create table book_author
(
    id_book   uuid not null references book (id_book),
    id_author uuid not null references author (id_author),
    primary key (id_book, id_author)
);

create table book_copy
(
    id_book_copy  uuid        not null constraint book_copy_pk primary key,
    id_book       uuid        not null references book (id_book),
    format        varchar(20) not null,
    selling_price float4,
    stock         integer     not null default 0
);

create table customer
(
    id_customer  uuid    not null constraint customer_pk primary key,
    first_name   varchar not null,
    last_name    varchar not null,
    phone_number varchar,
    email        varchar
);

create table sale
(
    id_sale        uuid        not null constraint sale_pk primary key,
    sale_date      timestamp   not null,
    payment_status varchar(20) not null,
    id_customer    uuid        references customer (id_customer)
);

create table sale_item
(
    id_sale_item uuid    not null constraint sale_item_pk primary key,
    id_sale      uuid    not null references sale (id_sale),
    id_copy      uuid    not null references book_copy (id_book_copy),
    quantity     integer not null,
    unit_price   float4  not null
);

create table stock_movement
(
    id_movement    uuid        not null constraint stock_movement_pk primary key,
    id_copy        uuid        not null references book_copy (id_book_copy),
    movement_type  varchar(10) not null,
    movement_date  timestamp   not null,
    quantity       integer     not null,
    sale_id        uuid        references sale (id_sale),
    reason         varchar
);