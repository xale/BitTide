package peer;

public enum PeerDownloadStatus
{
	notStarted	("download not started"),
	starting	("starting download"),
	inProgress	("downloading"),
	finishing	("finishing download"),
	done		("download complete"),
	failed		("download failed");

	private String statusMessage;

private PeerDownloadStatus(String message)
{
	statusMessage = message;	
}

public getStatusMessage()
{
	return statusMessage;
}

}