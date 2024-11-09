docker run -p 3306:3307 -dit --name resource-db -e MYSQL_ROOT_PASSWORD=password -e mysql:8.0

mysql -u root -p password
