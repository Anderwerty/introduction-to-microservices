CREATE TABLE song_metadata (
    id INTEGER UNIQUE,
    artist VARCHAR(100),
    name VARCHAR(100),
    album VARCHAR(100),
    duration VARCHAR(5),
    year_creation INTEGER,
    PRIMARY KEY (id)
);