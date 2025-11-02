CREATE TABLE storages (
    id SERIAL PRIMARY KEY,
    storage_type VARCHAR(50) NOT NULL,
    bucket VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL
);

INSERT INTO storage (id, storage_type, bucket, path)
VALUES (1, 'PERMANENT', 'permanent-bucket', '/permanent-files');

INSERT INTO storage (id, storage_type, bucket, path)
VALUES (2, 'STAGING', 'staging-bucket', '/staging-files')
