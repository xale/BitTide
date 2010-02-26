package peer;

import java.net.*;
import java.util.*;
import java.io.*;
import message.*;

public class IncomingPeerConnection implements Runnable
{
	private Socket peerSocket = null;
	private File downloadsDirectory = null;
	
public IncomingPeerConnection(Socket connectionSocket, File downloadsDir)
{
	peerSocket = connectionSocket;
	downloadsDirectory = downloadsDir;
}

private byte[] getBlock(RandomAccessFile inFile, String downloadFileName, int blockIndex) throws IOException
{
	byte[] retval;
	if (inFile == null)
	{
		retval = new byte[Message.MAX_BLOCK_SIZE];
		RandomAccessFile block = new RandomAccessFile(downloadFileName + "." + blockIndex, "r");
		block.read(retval);
	}
	else
	{
		inFile.seek((blockIndex - 1) * Message.MAX_BLOCK_SIZE);
		if ( blockIndex * Message.MAX_BLOCK_SIZE > inFile.length() )
		{
			retval = new byte[inFile.length() - (blockIndex - 1) * Message.MAX_BLOCK_SIZE];
		}
		else
		{
			retval = new byte[Message.MAX_BLOCK_SIZE];
		}
		inFile.readFully(retval);
	}
	return retval;
}

public void run()
{
	MessageInputStream readStream;
	MessageOutputStream writeStream;
	
	try
	{
		// Open streams to peer
		readStream = new MessageInputStream(peerSocket.getInputStream());
		writeStream = new MessageOutputStream(peerSocket.getOutputStream());
	}
	catch (Exception E)
	{
		// Failed to open streams; abort
		return;
	}

	try
	{
		Message message = readStream.readMessage();
		if (message.getMessageCode() != MessageCode.FileRequestMessageCode)
		{
			writeStream.writeMessage(new ErrorMessage("Expected file request, got message with code " + message.getMessageCode() + "."));
		}

		FileRequestMessage fileRequestMessage = (FileRequestMessage) message;
		
		File downloadFile = new File(downloadsDirectory, fileRequestMessage.getFilename());
		RandomAccessFile inFile = null;
		if (downloadFile.isFile())
		{
			// we have the whole file
			inFile = new RandomAccessFile(downloadFile, "r");
		}
		byte[] block;
		for (int blockNumber = fileRequestMessage.getBeginBlockIndex(); blockNumber <= FileRequestMessage.getEndBlockIndex(); ++blockNumber)
		{
			try
			{
				block = getBlock(inFile, downloadFile.getPath(), blockNumber);
			}
			catch (IOException e)
			{
				writeStream.writeMessage(new ErrorMessage("Could not find block " + blockNumber + " of file " + fileRequestMessage.getFilename() + ".");
			}
			writeStream.writeMessage(new FileReplyMessage(ByteBuffer.wrap(block)));
		}
	}
	catch (ErrorMessageException e)
	{
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	
	try
	{
		// Close write stream
		writeStream.close();
	}
	catch (IOException IOE)
	{
		// Do nothing...
	}
	
	try
	{
		// Close read stream
		readStream.close();
	}
	catch (IOException IOE)
	{
		// Do nothing...
	}
	
	try
	{
		// Close socket
		peerSocket.close();
	}
	catch (IOException IOE)
	{
		// Do nothing...
	}
}

}
