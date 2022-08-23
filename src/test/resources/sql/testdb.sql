drop table if exists booking;

drop table if exists patient;

drop table if exists contact_info;

drop table if exists premises;

drop table if exists address;

drop table if exists role_app_user;

drop table if exists app_role;

drop table if exists app_user;

create table if not exists address
(
    pk_address uuid default gen_random_uuid() not null
    primary key,
    city       text,
    street     text,
    zip_code   text
    );

alter table address
    owner to postgres;

create table if not exists app_role
(
    pk_app_role uuid default gen_random_uuid() not null
    primary key,
    user_role   text                           not null
    constraint user_role_unique
    unique
    );

alter table app_role
    owner to postgres;

create table if not exists app_user
(
    pk_app_user uuid default gen_random_uuid() not null
    primary key,
    password    text,
    username    text
    constraint username_unique
    unique
    );

alter table app_user
    owner to postgres;

create table if not exists contact_info
(
    pk_contact_info uuid default gen_random_uuid() not null
    primary key,
    email           text,
    phone           text,
    fk_user_address uuid
    constraint "foreign-key-user-address"
    references address
    on delete set null
    );

alter table contact_info
    owner to postgres;

create index if not exists "fki_foreign-key-user-address"
    on contact_info (fk_user_address);

create table if not exists patient
(
    pk_patient      uuid default gen_random_uuid() not null
    primary key,
    birth_date      date,
    first_name      text,
    last_name       text,
    pnr             text
    constraint unique_pnr
    unique,
    fk_contact_info uuid
    constraint foreign_key_contact_info
    references contact_info
    on delete set null,
    fk_app_user     uuid
    constraint foreign_key_user
    references app_user
    on update set null on delete set null
    );

alter table patient
    owner to postgres;

create index if not exists fki_k
    on patient (fk_contact_info);

create index if not exists fki_foreign_key_user
    on patient (fk_app_user);

create table if not exists premises
(
    pk_premises         uuid default gen_random_uuid() not null
    primary key,
    premises_name       text,
    fk_premises_address uuid
    constraint foreign_key_premises_address
    references address
    );

alter table premises
    owner to postgres;

create index if not exists fki_foreign_key_premises_address
    on premises (fk_premises_address);

create table if not exists role_app_user
(
    fk_app_user uuid not null
    constraint foreign_key_user
    references app_user,
    fk_app_role uuid not null
    constraint foreign_key_app_role
    references app_role
);

alter table role_app_user
    owner to postgres;

create index if not exists fki_foreign_key_app_role
    on role_app_user (fk_app_role);

create table if not exists booking
(
    pk_booking        uuid           default gen_random_uuid() not null
    primary key,
    "administratorId" text,
    date_time         timestamp,
    price             numeric(19, 2) default 0,
    vacant            boolean        default true,
    "vaccineType"     text,
    fk_patient        uuid
    constraint foreign_key_patient
    references patient,
    fk_premises       uuid
    constraint foreign_key_premises
    references premises
    );

alter table booking
    owner to postgres;

create index if not exists fki_foreign_key_premises
    on booking (fk_premises);

create index if not exists fki_foreign_key_patient
    on booking (fk_patient);

