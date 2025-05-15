create database if not exists demo;
create table balance
(
    id      bigint auto_increment primary key,
    userId  bigint         not null,
    balance decimal(10, 2) not null
);

create table orders
(
    id        bigint auto_increment primary key,
    productId varchar(50) not null,
    quantity  int         not null
);

create table stock
(
    id      bigint auto_increment primary key,
    product varchar(50) not null,
    stock   int         not null
);

