create table User (
    id bigint primary key auto_increment,
    pseudo varchar(400) not null,
    balance integer not null,
    password varchar(400) not null,
    modified timestamp(3) not null
);
create table Transaction (
    id bigint primary key auto_increment,
    source varchar(400) not null,
    destination varchar(400) not null,
    amount integer not null,
    modified timestamp(3) not null
);
create table Setting (
    id bigint primary key auto_increment,
    skey varchar(400) not null,
    value varchar(400) not null,
    modified timestamp(3) not null
);