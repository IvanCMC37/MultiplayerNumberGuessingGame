JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \
        ServerStage2.java \
		gameData.java \
        ServerThread.java \
        gameRecord.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) *.class