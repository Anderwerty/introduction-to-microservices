CREATE DATABASE resources_db;

USE resources_db;

CREATE TABLE resources (
    id int NOT NULL AUTO_INCREMENT,
    file BLOB,
    PRIMARY KEY (id)
);
