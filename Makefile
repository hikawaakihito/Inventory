JC = javac --module-path javafx-sdk-11.0.2/lib --add-modules javafx.controls
J = java -Djdk.gtk.version=2 -cp .:lib/* --module-path javafx-sdk-11.0.2/lib --add-modules javafx.controls

all:
	$(JC) $(shell find . -name "*.java")

run:
	$(J) Inventory