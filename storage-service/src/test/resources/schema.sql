CREATE TABLE storages (
    id SERIAL PRIMARY KEY,
    storage_type VARCHAR(50) NOT NULL,
    bucket VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL
);