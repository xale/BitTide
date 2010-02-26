package peer;

import java.io.*;
import java.nio.*;
import java.net.*;
import message.*;

public class OutgoingPeerConnection implements Runnable
{
	private PeerDownloadManager downloadManager = null;
	
	private Socket peerSocket = null;
	
	private String filename = null;
	private FileBitmap blocksRequested = null;
	private File downloadsDirectory = null;
	
public OutgoingPeerConnection(PeerDownloadManager manager, InetSocketAddress address, String nameOfFile, FileBitmap blocksToDownload, File destinationDirectory)
	throws IOException
{
	downloadManager = manager;
	filename = nameOfFile;
	blocksRequested = blocksToDownload;
	downloadsDirectory = destinationDirectory;
	
	// Attempt to open a socket to the peer
	peerSocket = new Socket(address.getAddress(), address.getPort());
}

public void run()
{
	try
	{
		// Open streams on the socket to the peer
		MessageInputStream readStream = new MessageInputStream(peerSocket.getInputStream());
		MessageOutputStream writeStream = new MessageOutputStream(peerSocket.getOutputStream());
		
		// Request each of the blocks from the peer
		int startIndex = 1, endIndex, numRequested, numReceived;
		while (startIndex <= FileBitmap.FILE_BITMAP_SIZE)
		{
			// Check if we need this block from this peer
			if (!blocksRequested.get(startIndex))
			{
				startIndex++;
				continue;
			}
			
			// Determine if this block is part of a contiguous range
			
			for (endIndex = startIndex + 1; endIndex <= FileBitmap.FILE_BITMAP_SIZE; endIndex++)
			{
				if (!blocksRequested.get(endIndex))
					break;
			}
			
			// Request the range from the peer
			writeStream.writeMessage(new FileRequestMessage(filename, startIndex, (endIndex - 1)));
			
			numRequested = endIndex - startIndex;
			for (numReceived = 0; numReceived < numRequested; numReceived++)
			{
				// Wait for the response
				Message replyMessage = readStream.readMessage();
				
				// Check that the response is a MSG_FILE_REP
				if (replyMessage.getMessageCode() != MessageCode.FileReplyMessageCode)
					throw new Exception("FileRequest response is not a FileReply");
				
				FileReplyMessage fileReply = (FileReplyMessage)replyMessage;
				
				// Attempt to update the file's bitmap; if the download manager returns "false," abandon the download
				if (!downloadManager.blockReceived(filename, fileReply.getBlockIndex()))
					throw new Exception("download canceled");
				
				// Write the received block to disk
				File downloadLocation = new File(downloadsDirectory, filename + "." + fileReply.getBlockIndex());
				FileOutputStream foutStream = new FileOutputStream(downloadLocation);
				foutStream.write(fileReply.getBlockContents().array());
				foutStream.close();
			}
			
			// Advance indexes
			startIndex = endIndex + 1;
		}
	}
	catch (Exception E)
	{
		// Inform the download manager that the download has failed
		downloadManager.downloadFailed(filename);
	}
	finally
	{
		// Close socket before ending
		try
		{
			if (!peerSocket.isClosed())
				peerSocket.close();
		}
		catch (Exception E)
		{
			// Ignore; there's nothing meaningful we can do here
		}
	}
}

private void writeBlock(int blockIndex, ByteBuffer blockContents)
{

}

}
