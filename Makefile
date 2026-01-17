all: build deploy

build:
	mvn package

deploy:
	cp ./target/UpgradableChest-1.0-SNAPSHOT.jar ../server-files/mods
