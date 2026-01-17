all: build deploy
	

build:
	mvn package

deploy:
	cp ./target/ExamplePlugin-1.0-SNAPSHOT.jar ../mods
