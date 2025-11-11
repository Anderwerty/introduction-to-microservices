CREATE TABLE resources (
    id SERIAL PRIMARY KEY,
    file_state VARCHAR(255) NOT NULL,
    full_url VARCHAR(255) NOT NULL,
    bucket_name VARCHAR(255) NOT NULL,
    s3_key VARCHAR(255) NOT NULL
);