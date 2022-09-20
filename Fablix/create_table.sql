DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies (
    id varchar(10) not null default '',
    title varchar(100) not null default '',
    year integer not null,
    director varchar(100) not null default '',
    PRIMARY KEY (id)
);

CREATE TABLE stars (
    id varchar(10) not null default '',
    name varchar(100) not null default '',
    birthYear integer,
    PRIMARY KEY (id)
);

CREATE TABLE stars_in_movies (
    starId varchar(10) not null default '',
    movieId varchar(10) not null default '',
    FOREIGN KEY(starId) REFERENCES stars(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards(
    id varchar(20),
    firstName varchar(50) not null default '',
    lastname varchar(50) not null default '',
    expiration date not null,
    PRIMARY KEY(id)
);

CREATE TABLE genres (
    id integer not null AUTO_INCREMENT,
    name varchar(32) not null default '',
    PRIMARY KEY(id)
);

CREATE TABLE genres_in_movies(
    genreId integer not null,
    movieId varchar(10) not null default '',
    FOREIGN KEY(genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE customers(
    id integer not null AUTO_INCREMENT,
    firstName varchar(50) not null default '',
    lastname varchar(50) not null default '',
    ccId varchar(20) not null default '',
    address varchar(200) not null default '',
    email varchar(50) not null default '',
    password varchar(20) not null default '',
    PRIMARY KEY(id),
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales(
    id integer not null,
    customerId integer not null,
    movieId varchar(10) not null default '',
    saleDate Date not null,
    FOREIGN KEY(movieId) REFERENCES movies(id),
    FOREIGN KEY(customerId) REFERENCES customers(id)
);

CREATE TABLE ratings(
    movieId varchar(10) not null default '',
    rating float not null,
    numVotes integer not null,
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));