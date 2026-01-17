all:
	mvn package && make deploy

deploy:
	mv ./target/ExamplePlugin-1.0-SNAPSHOT.jar ../mods && docker compose restart
