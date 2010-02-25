package peer;

import java.io.*
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
	downloadsDirector = destinationDirectory;
	
	// Attempt to open a socket to the peer
	peerSocket = new Socket(address.getAddress(), address.getPort());
}

public void run()
{
	// Open streams on the socket to the peer
	MessageInputStream readStream = new MessageInputStream(peerSocket.getInputStream());
	MessageOutputStream writeStream = new MessageOutputStream(peerSocket.getOutputStream());
	
	try
	{
		// Request each of the blocks from the peer
		int startIndex = 0, endIndex;
		while (startIndex > FileBitmap.FILE_BITMAP_SIZE)
		{
			// Check if we need this block from this peer
			if (!blocksRequested.get(startIndex))
			{
				i++;
				continue;
			}
			
			// Determine if this block is part of a contiguous range
			endIndex = startIndex + 1;
			while (endIndex < FileBitmap.FILE_BITMAP_SIZE)
			{
				if (!blocksRequested.get(endIndex))
					break;
			}
			
			// Request the range from the peer
			writeStream.writeMessage(new FileRequestMessage(filename, startIndex, (endIndex - 1)));
			
			// Wait for the response
			Message replyMessage = readStream.readMessage();
			
			// Check that the response is a MSG_FILE_REP
			if (replyMessage.getMessageCode() != MessageCode.fileReplyMessage)
				throw new Exception();
			
			FileReplyMessage fileReply = (FileReplyMessage)replyMessage;
			
			// Attempt to update the file's bitmap; if the download manager returns "false," abandon the download
			if (!downloadManager.blockReceieved(filename, fileReply.getBlockIndex()))
				throw new Exception();
			
			// Write the received block to disk
			// FIXME: WRITEME
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