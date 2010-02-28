package peer;

import message.*;

public class PeerDownloadFile
{
	private long fileSize = 0;
	private FileBitmap receivedBitmap;
	private PeerDownloadStatus downloadStatus = PeerDownloadStatus.notStarted;
	private String failureReason;

public PeerDownloadFile(long size)
{
	fileSize = size;
	receivedBitmap = new FileBitmap();
	failureReason = null;
}

public long getFileSize()
{
	return fileSize;
}

public FileBitmap getReceivedBitmap()
{
	return receivedBitmap;
}

public void updateReceivedBitmap(int receivedBlockIndex)
{
	receivedBitmap.setHasBlockAtIndex(receivedBlockIndex);
}

public PeerDownloadStatus getDownloadStatus()
{
	return downloadStatus;
}

public void setDownloadStatus(PeerDownloadStatus status)
{
	downloadStatus = status;
}

public String getFailureReason()
{
	return failureReason;
}

public void setFailureReason(String reason)
{
	failureReason = reason;
}

}
