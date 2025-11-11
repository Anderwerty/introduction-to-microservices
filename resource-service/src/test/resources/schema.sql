CREATE TABLE resources (
    id SERIAL PRIMARY KEY,
    file_state VARCHAR(255),
    full_url VARCHAR(255),
    bucket_name VARCHAR(255),
    s3_key VARCHAR(255)
);