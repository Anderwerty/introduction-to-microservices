docker run -dit --name song-db-postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=song_metadata_db -d postgres:16

psql -h localhost -p 5432 -U postgres -d song_metadata_db
