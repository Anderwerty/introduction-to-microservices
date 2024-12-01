CREATE TABLE song_metadata (
    `id` int NOT NULL,
    `artist` VARCHAR(120),
    `name` VARCHAR(120),
    `album` VARCHAR(120),
    `duration` VARCHAR(120),
    `year_creation` int(4),
    PRIMARY KEY (`id`)
);