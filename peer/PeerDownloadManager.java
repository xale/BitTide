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

public synchronized boolean startDownload(String filename, SearchReplyMessage downloadInfo)
{
	// Check if we have already downloaded part or all of this file
	PeerDownloadFile download = downloads.get(filename);
	if (download != null)
	{
		// Determine the state of the previous download for this file
		switch (download.getDownloadStatus())
		{
			case notStarted:
			case restarting:
			case inProgress:
			case finishing:
			{
				// Download already in progress; inform the user
				System.out.println();
				System.out.println("the file " + filename + " is already being downloaded");
				return;
			}
			case complete:
			{
				// Ask if the user wants to re-download the file
				System.out.println();
				System.out.println("the file " + filename + " has already been downloaded");
				System.out.print("would you like to download the file again? (y/N) ");
				
				// Read the user's response
				String response = new Scanner(System.in).nextLine();
				
				// If the user doesn't want to re-download, abort
				try
				{
					if (response.charAt(0) != 'y')
						return;
				}
				catch (IndexOutOfBoundsException noResponse)
				{
					return;
				}
				
				// Otherwise, restart the download
				System.out.print("beginning download... ");
				download = new PeerDownloadFile(download.getFileSize());
				download.setDownloadStatus(PeerDownloadStatus.restarting);
				downloads.put(filename, download);
				
				break;
			}	
			case canceled:
			case failed:
			{
				// Restart the download
				download.setDownloadStatus(PeerDownloadStatus.restarting);
				
				break;
			}	
		}
	}
	// Otherwise, create a new DownloadFile entry, to track the state of this download
	else
	{
		// Create the download information object
		download = new PeerDownloadFile(downloadInfo.getFileSize());
		
		// Map the filename to the download info
		downloads.put(filename, download);
		
		// Add the name of this file to the download list
		downloadList.add(filename);
		
		// Check if we already have any parts of this file
		File[] partialFilesOnDisk = this.getPartialFiles(filename);
		String partialFileName;
		int blockNumber;
		for (int i = 0; i < partialFilesOnDisk.length; i++)
		{
			// Get the name of the file
			partialFileName = partialFilesOnDisk[i].getName();
			
			// Get the block number of this partial file
			blockNumber = Integer.parseInt(partialFileName.substring(filename.length() + 1));
			
			// Add the block number to the bitmap
			download.updateReceivedBitmap(blockNumber);
		}
	}
	
	// Get the list of peers seeding the file
	SearchReplyPeerEntry[] peers = downloadInfo.getPeerResults();
	
	// XOR the bitmap of the file that we have with the bitmap of the full file to determine the blocks of the file we are missing
	FileBitmap blocksNeeded = new FileBitmap(downloadInfo.getFileSize());
	blocksNeeded.xor(download.getReceivedBitmap());
	
	// FIXME: debug implementation
	FileBitmap commonBlocks;
	for (SearchReplyPeerEntry peer : peers)
	{
		commonBlocks = new FileBitmap(peer.getFileBitmap());
		commonBlocks.and(blocksNeeded);
		
		if (commonBlocks.equals(blocksNeeded))
		{
			// Start a download thread
			try
			{
				threadPool.execute(new OutgoingPeerConnection(this, peer.getAddress(), filename, commonBlocks, downloadsDirectory));
				return;
			}
			catch (IOException IOE)
			{
				// Try the next peer
			}
		}
	}
	
	// If not all blocks could be located, cancel the download
	this.downloadFailed(filename, "not all blocks available");
}

public synchronized void choosePeers(String filename, SearchReplyPeerEntry[] peers, FileBitmap blocksNeeded)
{
	/*
	// Construct a priority queue to sort the block indexes by their rarity
	PriorityQueue<BlockIndexFrequency> blockFrequencies = new PriorityQueue<BlockIndexFrequency>();
	for (int i = 1; i <= FileBitmap.FILE_BITMAP_SIZE; i++)
	{
		if (blocksNeeded.hasBlockAtIndex(i))
			blockFrequencies.add(new BlockIndexFrequency(i, peers));
	}
	
	// Request the rarest blocks first
	while (blockFrequencies.size() > 0)
	{
		// Check that the frequency is greater than zero
	}
	*/
	
	/*
	Set sortedPeers = new TreeSet(
	int maxRangeSize = (blocksNeeded.getNumberOfBlocks() / peers.length);
	FileBitmap blocksRemaining = blocksNeeded.clone();
	
	SearchReplyPeerEntry peer;
	int startIndex = 1, endIndex;
	while (int startIndex
	{
		for (int startIndex = 1; startIndex <= FileBitmap.FILE_BITMAP_SIZE; startIndex++)
		{
			if (blocksRemaining.hasBlockAtIndex(startIndex))
				continue;
			
			for (SearchReplyPeerEntry peer : sortedPeers)
			{
				if (
			}
		}
	}
	*/
}

public synchronized void stopDownloads(boolean sendBitmaps)
{
	// Iterate through the list of downloads
	for (Map.Entry<String, PeerDownloadFile> file : downloads.entrySet())
	{
		// If this is not a download in-progress, ignore it
		if (file.getValue().getDownloadStatus().isStopped())
			continue;
		
		// Cancel downloads in progress
		file.getValue().setDownloadStatus(PeerDownloadStatus.canceled);
		
		// If requested, send the most up-to-date bitmaps to the tracker
		if (sendBitmaps)
		{
			try
			{
				trackerConnection.sendMessage(new FileBitmapMessage(file.getKey(), file.getValue().getReceivedBitmap()));
			}
			catch (IOException IOE)
			{
				// Nothing we can do here...
			}
			catch (ErrorMessageException EME)
			{
				// Nothing we can do here...
			}
		}
	}
}

public synchronized void shutdown()
{
	// Stop accepting new tasks to the thread pool
	threadPool.shutdown();
	
	// Cancel all downloads in-progress
	this.stopDownloads(false);
	
	try
	{
		// If the existing downloads don't finish in five seconds, force termination
		if (!threadPool.awaitTermination(5, TimeUnit.SECONDS))
			threadPool.shutdownNow();
	}
	catch (InterruptedException E)
	{
		// If the timeout is interrupted, force termination immediately
		threadPool.shutdownNow();
	}
	
	// Remove partial downloads on disk
	for (Map.Entry<String, PeerDownloadFile> file : downloads.entrySet())
	{
		this.deletePartialDownload(file.getKey());
	}
}

public synchronized boolean wantsBlocksForFile(String filename)
{
	return !(downloads.get(filename).getDownloadStatus().isStopped());
}

public synchronized void blockReceived(String filename, int blockIndex)
{
	// Locate the file to update
	PeerDownloadFile file = downloads.get(filename);
	
	// Update the bitmap
	file.updateReceivedBitmap(blockIndex);
	
	// Send the updated bitmap to the tracker
	try
	{
		trackerConnection.sendMessage(new FileBitmapMessage(filename, file.getReceivedBitmap()));
	}
	catch (IOException IOE)
	{
		// Not much we can do here; for now, continue downloading, and hope we can update the tracker when the next block is received or when we log out
	}
	catch (ErrorMessageException EME)
	{
		// Nothing we can do here...
	}
	
	// If all the blocks have been downloaded, merge the partial files
	FileBitmap completeBitmap = new FileBitmap(file.getFileSize());
	if (file.getReceivedBitmap().equals(completeBitmap))
	{
		file.setDownloadStatus(PeerDownloadStatus.finishing);
		try
		{
			finalizeFile(filename);
			deletePartialFiles(filename);
		}
		catch (IOException e)
		{
			System.err.println(e.getMessage());
			file.setDownloadStatus(PeerDownloadStatus.failed);
		}
		file.setDownloadStatus(PeerDownloadStatus.complete);
	}
	// If the download is incomplete, but hasn't been marked as in-progress, mark it as such
	else if (file.getDownloadStatus() == PeerDownloadStatus.notStarted || file.getDownloadStatus() == PeerDownloadStatus.restarting)
	{
		file.setDownloadStatus(PeerDownloadStatus.inProgress);
	}
}

private synchronized void finalizeFile(String filename)
	throws IOException
{
	File downloadFile = new File(downloadsDirectory, filename);
	File[] partialFiles = getPartialFiles(filename);
	FileOutputStream ofstream = new FileOutputStream(downloadFile);
	FileInputStream ifstream = null;
	byte[] contents;
	try
	{
		for (File partialFile : partialFiles)
		{
			ifstream = new FileInputStream(partialFile);
			contents = new byte[(int) partialFile.length()];
			ifstream.read(contents);
			ofstream.write(contents);
			ifstream.close();
		}
	}
	finally
	{
		if (ifstream != null)
			ifstream.close();
		ofstream.close();
	}
}

public synchronized void downloadFailed(String filename, String reason)
{
	// Locate the file in question
	PeerDownloadFile file = downloads.get(filename);
	
	// If the download has not already been stopped for other reasons, update the status
	if (!file.getDownloadStatus().isStopped())
	{
		// Set download status to "failed"
		file.setDownloadStatus(PeerDownloadStatus.failed);
		
		// Record the reason the download failed
		file.setFailureReason(reason);
	}
}

public synchronized File[] getPartialFiles(String filename)
{
	// Return the list of files in the downloads directory that represent partial segments of the file with the specified name
	return downloadsDirectory.listFiles(new PartialFilesFilenameFilter(filename));
}

public synchronized int deletePartialFiles(String filename)
{
	// Delete all the partial files for this download
	File[] partialFiles = this.getPartialFiles(filename);
	for (File file : partialFiles)
	{
		file.delete();
	}
	
	// Return the number of files deleted
	return partialFiles.length;
}

public synchronized void deletePartialDownload(String filename)
{
	// Delete all partial files associated with this download
	int numFilesDeleted = this.deletePartialFiles(filename);
	
	// If we deleted anything, send an empty bitmap to the tracker, indicating that we don't have this file
	if (numFilesDeleted > 0)
	{
		try
		{
			trackerConnection.sendMessage(new FileBitmapMessage(filename, new FileBitmap()));
		}
		catch (IOException IOE)
		{
			// Nothing we can do here...
		}
		catch (ErrorMessageException EME)
		{
			// Nothing we can do here...
		}
	}
}

public synchronized void printDownloadStatusList()
{
	// Check if we have any downloads to list
	if (downloadList.size() < 1)
	{
		System.out.println("no downloads to list");
		return;
	}
	
	// Iterate through the list of downloads, in the order they were added
	PeerDownloadFile file;
	FileBitmap bitmap;
	for (String filename : downloadList)
	{
		// Get the download entry
		file = downloads.get(filename);
		
		// Print the name and status of the file
		System.out.print(filename + " (" + file.getDownloadStatus());
		if (file.getDownloadStatus() == PeerDownloadStatus.failed)
		{
			System.out.println(": " + file.getFailureReason() + ")");
		}
		else
		{
			System.out.println(")");
		}
		
		// Calculate the download progress
		bitmap = file.getReceivedBitmap();
		int downloadedBlocks = bitmap.getNumberOfBlocks();
		int totalBlocks = (int)Math.ceil((double)file.getFileSize() / (double)Message.MAX_BLOCK_SIZE);
		
		// Print the download progress
		System.out.println("  [" + bitmap.getStringForBitmapOfSize(totalBlocks) + "]");
		System.out.println("  " + downloadedBlocks + " out of " + totalBlocks + " blocks");
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
