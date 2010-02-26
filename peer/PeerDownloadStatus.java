package peer;

public enum PeerDownloadStatus
{
	notStarted	("starting download"),
	inProgress	("download in progress"),
	finishing	("finishing download"),
	complete	("download complete"),
	canceled	("download canceled"),
	restarting	("restarting download"),
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
	return (this == complete) || (this == canceled) || (this == failed);
}

}