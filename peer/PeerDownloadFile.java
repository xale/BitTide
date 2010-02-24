package peer;

import message.*;

public class PeerDownloadFile
{
	private long fileSize = 0;
	private FileBitmap receivedBitmap = null;
	private PeerDownloadStatus downloadStatus = PeerDownloadStatus.notStarted;
	
public PeerDownloadFile(long size)
{
	fileSize = size;
	receivedBitmap = new FileBitmap();
}

public long getFileSize()
{
	return fileSize;
}

public FileBitmap getReceivedBitmap()
{
	return receivedBitmap;
}

public void updateReceivedBitmap(FileBitmap update)
{
	receivedBitmap.or(update);
}

public PeerDownloadStatus getDownloadStatus()
{
	return downloadStatus;
}

public void setDownloadStatus(PeerDownloadStatus status)
{
	downloadStatus = status;
}

}
