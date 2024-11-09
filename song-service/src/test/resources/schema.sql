CREATE TABLE song_metadata (
    `id` int NOT NULL AUTO_INCREMENT,
    `artist` VARCHAR(120),
    `name` VARCHAR(120),
    `album` VARCHAR(120),
    `length` VARCHAR(120),
    `resource_Id` int NOT NULL,
    `year_creation` int(4),
    PRIMARY KEY (`id`)
);