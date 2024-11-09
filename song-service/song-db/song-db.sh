docker run -dit --name song-db -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -d mysql:8.0

mysql -u root -p password
