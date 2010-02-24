package peer;

import java.io.*;
import java.util.concurrent.*;
import message.*;

public class PeerDownloadManager
{
	private File downloadsDirectory = null;
	
	private PeerToTrackerConnection trackerConnection = null;
	
	private ExecutorService threadPool = null;
	
public PeerDownloadManager(PeerToTrackerConnection tracker, File downloadDir)
{
	downloadsDirectory = downloadDir;
	trackerConnection = tracker;
	
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

public synchronized void updateDownloadStatus()
{
	// FIXME: WRITEME
}

}
