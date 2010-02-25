package peer;

public enum PeerDownloadStatus
{
	notStarted	("starting download"),
	inProgress	("download in progress"),
	finishing	("finishing download"),
	done		("download complete"),
	canceled	("download canceled"),
	resuming	("resuming download"),
	failed		("download failed");

	private String statusMessage;

private PeerDownloadStatus(String message)
{
	statusMessage = message;	
}

public String getStatusMessage()
{
	return statusMessage;
}

public String toString()
{
	return statusMessage;
}

public boolean isStopped()
{
	return (this == done) || (this == canceled) || (this == failed);
}

}