create table if not exists military_rank (
    id bigint auto_increment primary key,
    name varchar(255) not null unique
);

create table if not exists serviceman (
    id bigint auto_increment primary key,
    last_name varchar(255),
    first_name varchar(255),
    patronymic varchar(255),
    nationality varchar(255),
    birth_date date,
    position varchar(255),
    postal_code varchar(255),
    country varchar(255),
    region varchar(255),
    district varchar(255),
    city varchar(255),
    street varchar(255),
    house varchar(255),
    apartment varchar(255),
    rank_id bigint not null,
    foreign key (rank_id) references military_rank(id)
);
