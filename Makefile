pack:
	dotnet pack src/Toadstool -o ../../out --include-symbols --include-source -c Release

local:
	dotnet pack src/Toadstool/ -o ../../out --version-suffix=beta-`date +%s`
	nuget push out/*.nupkg -Source Local

.PHONY: test
test:
	./gradlew test --info

clean:
	rm -vrf build

clean-databases: 
	docker-compose down -v

wait-for=docker-compose run --rm wait-for -t 90

docker-compose-up:
	docker-compose up --no-start

postgres: docker-compose-up
	docker-compose start postgres
	$(wait-for) toadstool_postgres_db:5432 

sqlserver: docker-compose-up
	docker-compose start sqlserver
	$(wait-for) toadstool_sqlserver_db:1433
	docker exec -it toadstool_sqlserver_db bash ./import-data.sh

databases: sqlserver postgres