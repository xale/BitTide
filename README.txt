Alex Heinz
Alex Rozenshteyn
CS344

BUILDING:
run 'make' in the root package directory

RUNNING:
Running the peer:
java peer.Peer <tracker address> <tracker port> <peer listen port> <username> <password> <downloads directory>

Running the tracker:
java tracker.Network <tracker listen port> <user database filename>

NOTES:

Things that work in the peer:
	- connect to tracker
	- login to tracker
	- send list of seeded files to tracker
	- find files on tracker
	- download blocks of an available file, if one peer has all of it
	- update tracker with downloaded blocks
	- print list of files downloaded or downloading
	- stop downloads in progress
	- log out
	- quit
	- clean up temporary files

Things that don't work:
	- doesn't concurrent downloads from multiple peers
	- downloads of a file that no peer has a complete copy of
	- download gets stuck on last block
	- can't upload files

Things that work in tracker:
	- peers can connect
	- peers can log in
	- peer database maintained
	- database read from and written to disk on startup and close
	- handles all valid message types
	- peers can log out

Things that don't work
	- doesn't find valid files when a user searches for them
