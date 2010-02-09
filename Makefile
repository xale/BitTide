ALL=main
all=$(ALL)

main:
	javac *.java

clean:
	rm -rf *.class
