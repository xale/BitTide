ALL=clean tracker peer
all: $(ALL)
main: $(ALL)

# Build rules
tracker: tracker/Tracker.class
peer: peer/Peer.class

# Cleaning rules
.PHONY:clean
clean:
	rm -rf message/*.class peer/*.class tracker/*.class

# Dependencies
peer/Peer.class: peer/Peer.java
	javac $^
	
tracker/Tracker.class: tracker/Network.java
	javac $^
