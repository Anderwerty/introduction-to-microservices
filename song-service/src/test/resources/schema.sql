CREATE TABLE song_metadata (
    `id` int NOT NULL,
    `artist` VARCHAR(100),
    `name` VARCHAR(100),
    `album` VARCHAR(100),
    `duration` VARCHAR(5),
    `year_creation` int(4),
    PRIMARY KEY (`id`)
);