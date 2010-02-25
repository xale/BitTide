package peer;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import message.*;

public class PeerDownloadManager
{
	private File downloadsDirectory = null;
	private Map<String, PeerDownloadFile> downloads = null;
	private List<String> downloadList = null;
	
	private PeerToTrackerConnection trackerConnection = null;
	
	private ExecutorService threadPool = null;
	
public PeerDownloadManager(PeerToTrackerConnection tracker, File downloadDir)
{
	downloadsDirectory = downloadDir;
	trackerConnection = tracker;
	
	// Create a map of filenames to download information objects
	downloads = new HashMap<String, PeerDownloadFile>();
	
	// Create a list of filenames, maintaining the order in which they were downloaded
	downloadList = new LinkedList<String>();
	
	// Create a pool of worker threads
	threadPool = Executors.newCachedThreadPool();
}

public synchronized void startDownload(String filename, SearchReplyMessage downloadInfo)
{
	// FIXME: WRITEME
}

public synchronized void stopDownloads()
{
	// Iterate through the list of downloads
	for (Map.Entry<String, PeerDownloadFile> file : downloads.entrySet())
	{
		// If this is not a download in-progress, ignore it
		if (file.getValue().getDownloadStatus().isStopped())
			continue;
		
		// Cancel downloads in progress
		file.getValue().setDownloadStatus(PeerDownloadStatus.canceled);
	}
}

public synchronized void shutdown()
{
	// Stop accepting new tasks to the thread pool
	threadPool.shutdown();
	
	// Cancel all downloads in-progress
	this.stopDownloads();
	
	try
	{
		// If the existing downloads don't finish in ten seconds, force termination
		if (!threadPool.awaitTermination(10, TimeUnit.SECONDS))
			threadPool.shutdownNow();
	}
	catch (InterruptedException E)
	{
		// If the timeout is interrupted, force termination immediately
		threadPool.shutdownNow();
	}
}

public synchronized boolean blockReceived(String filename, int blockIndex)
{
	// Locate the file to update
	PeerDownloadFile file = downloads.get(filename);
	
	// Check if the file is still being downloaded
	if (file.getDownloadStatus().isStopped())
		return false;
	
	// Update the bitmap
	file.updateReceivedBitmap(blockIndex);
	
	// Send the updated bitmap to the tracker
	try
	{
		trackerConnection.sendMessage(new FileBitmapMessage(filename, file.getReceivedBitmap()));
	}
	catch (Exception E)
	{
		// FIXME: WRITEME
		return false;
	}
	
	// If necessary, update the download status
	if (file.getDownloadStatus() == PeerDownloadStatus.notStarted)
		file.setDownloadStatus(PeerDownloadStatus.inProgress);
	
	return true;
}

public synchronized void downloadFailed(String filename)
{
	// Locate the file in question
	PeerDownloadFile file = downloads.get(filename);
	
	// If the download has not already been stopped for other reasons, update the status
	if (!file.getDownloadStatus().isStopped())
		file.setDownloadStatus(PeerDownloadStatus.failed);
}

public synchronized void printDownloadStatusList()
{
	// Iterate through the list of downloads, in the order they were added
	PeerDownloadFile file;
	for (String filename : downloadList)
	{
		// Get the download entry
		file = downloads.get(filename);
		
		// Print the name and status of the file
		System.out.println(filename + " (" + file.getDownloadStatus() + "):");
		
		// Calculate the download progress
		int downloadedBlocks = file.getReceivedBitmap().getNumberOfBlocks();
		int totalBlocks = (int)Math.ceil((double)file.getFileSize() / (double)Message.MAX_BLOCK_SIZE);
		
		// FIXME: calculate bytes downloaded out of total bytes?
		
		// Print the download progress
		System.out.println("\t" + downloadedBlocks + " out of " + totalBlocks + " blocks");
	}
}

public synchronized int getNumDownloadsInProgress()
{
	int countInProgress = 0;
	
	// Iterate through the list of downloads
	for (Map.Entry<String, PeerDownloadFile> file : downloads.entrySet())
	{
		// If this is a download in-progress, increment the count
		if (!file.getValue().getDownloadStatus().isStopped())
			countInProgress++;
	}
	
	return countInProgress;
}

}
