docker run -dit --name resource-db -p 3307:3306 -e MYSQL_ROOT_PASSWORD=password -d mysql:8.0

mysql -u root -p password
