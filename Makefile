all: build deploy

build:
	mvn package

deploy:
	cp ./target/BackpackTorch-1.0-SNAPSHOT.jar ../server-files/mods
