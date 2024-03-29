assemble:
	./gradlew assemble

test:
	./gradlew test --info

clean:
	./gradlew clean

clean-databases: 
	docker-compose down -v

wait-for=docker-compose run --rm wait-for -t 90

docker-compose-up:
	docker-compose build
	docker-compose up --no-start

postgres: docker-compose-up
	docker-compose start postgres
	$(wait-for) toadstool_postgres_db:5432

sqlserver: docker-compose-up
	docker-compose start sqlserver
	$(wait-for) toadstool_sqlserver_db:1433
	docker exec -it toadstool_sqlserver_db bash ./import-data.sh

mysql: docker-compose-up
	docker-compose start mysql
	$(wait-for) toadstool_mysql_db:3306

databases: sqlserver postgres mysql