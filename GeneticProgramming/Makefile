SRCS = $(wildcard *.java)
CLASSES = $(SRCS:.java=.class)

all: $(CLASSES)

%.class: %.java
	javac $<

clean:
	rm -f *.class

run: all
	java Main
