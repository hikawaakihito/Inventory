JC = javac --module-path /opt/javafx-sdk-11.0.2/lib --add-modules javafx.controls
J = java -cp .:lib/* --module-path /opt/javafx-sdk-11.0.2/lib --add-modules javafx.controls

all:
	$(JC) $(shell find . -name "*.java")

run:
	$(J) Inventory