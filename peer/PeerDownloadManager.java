package peer;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import message.*;

public class PeerDownloadManager
{
	private File downloadsDirectory = null;
	private Map<String, PeerDownloadFile> downloads = null;
	
	private PeerToTrackerConnection trackerConnection = null;
	
	private ExecutorService threadPool = null;
	
public PeerDownloadManager(PeerToTrackerConnection tracker, File downloadDir)
{
	downloadsDirectory = downloadDir;
	trackerConnection = tracker;
	
	// Create a map of filenames to download information objects
	downloads = new HashMap<String, PeerDownloadFile>();
	
	// Create a pool of worker threads
	threadPool = Executors.newCachedThreadPool();
}

public synchronized void startDownload(String filename, SearchReplyMessage downloadInfo)
{
	// FIXME: WRITEME
}

public synchronized void stopDownloads()
{
	// Stop accepting new tasks to the thread pool
	threadPool.shutdown();
	
	try
	{
		// If the current tasks don't finish in ten seconds, force termination
		if (!threadPool.awaitTermination(10, TimeUnit.SECONDS))
			threadPool.shutdownNow();
	}
	catch (InterruptedException E)
	{
		// If the timeout is interrupted, force termination immediately
		threadPool.shutdownNow();
	}
}

public synchronized void updateDownloadStatus(String filename, PeerDownloadStatus status)
{
	// Locate the file to update
	PeerDownloadFile file = downloads.get(filename);
	
	// If the file is in a "stopped" state, (i.e., finished downloading, failed, or canceled) do not change its status
	if (file.getDownloadStatus().isStopped())
		return;
	
	// Otherwise, update the status
	file.setDownloadStatus(status);
}

public synchronized PeerDownloadStatus getDownloadStatus(String filename)
{
	return downloads.get(filename).getDownloadStatus();
}

public synchronized void updateDownloadBitmap(String filename, FileBitmap bitmap)
{
	// Locate the file to update
	PeerDownloadFile file = downloads.get(filename);
	
	// Update the bitmap
	file.updateReceivedBitmap(bitmap);
	
	// If necessary, update the download status
	if (file.getDownloadStatus() == PeerDownloadStatus.notStarted)
		file.setDownloadStatus(PeerDownloadStatus.inProgress);
}

public synchronized void printDownloadStatusList()
{
	// FIXME: WRITEME
}

public synchronized int getNumDownloadsInProgress()
{
	// FIXME: WRITEME
	return 0;
}

}
