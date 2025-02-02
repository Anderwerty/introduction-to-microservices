docker run -dit --name resource-db-postgres -p 5433:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=resources_db -d postgres:16

psql -h localhost -p 5432 -U postgres -d resources_db
