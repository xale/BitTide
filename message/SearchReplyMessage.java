package message;

import java.net.*;
import java.nio.*;
import java.util.*;

public class SearchReplyMessage extends Message
{
	private long fileSize;
	private SearchReplyPeerEntry[] peerResults;

/**
* Creates a SearchReplyMessage indicating that no results were found.
*/
public SearchReplyMessage()
{
	this(0, null);
}

public SearchReplyMessage(long sizeOfFile, SearchReplyPeerEntry[] peers)
{
	fileSize = sizeOfFile;
	peerResults = peers;
}

public SearchReplyMessage(ByteBuffer contents)
{
	// Check if the reply contains any results
	if (contents == null)
	{
		fileSize = 0;
		peerResults = null;
		return;
	}
	
	// Retrieve the size of the file
	fileSize = ByteBufferUtils.getUnsignedIntFrom(contents);
	
	// Determine the number of peers returned in the search result
	int numPeers = ((contents.array().length - Message.FILESIZE_FIELD_WIDTH) / Message.PEER_ENTRY_WIDTH);
	
	// Parse the entries
	peerResults = new SearchReplyPeerEntry[numPeers];
	int numValidResults = 0;
	for (int i = 0; i < numPeers; i++)
	{
		try
		{
			// Read the peer's ip address
			byte[] ip = new byte[Message.IP_FIELD_WIDTH];
			contents.get(ip);
			
			// Read the peer's listening port
			int port = ByteBufferUtils.getUnsignedShortFrom(contents);
			
			// Read the peer's bitmap of the file
			byte[] fileBitmap = new byte[Message.BITMAP_FIELD_WIDTH];
			contents.get(fileBitmap);
			
			// Create the entry
			peerResults[i] = new SearchReplyPeerEntry(ip, port, fileBitmap);
			numValidResults++;
		}
		catch (UnknownHostException UHE)
		{
			peerResults[i] = null;
		}
	}
	
	// Trim out bad entries
	SearchReplyPeerEntry[] validResults = new SearchReplyPeerEntry[numValidResults];
	int j = 0;
	for (int i = 0; i < numValidResults; i++)
	{
		if (peerResults[i] != null)
		{
			validResults[j] = peerResults[i];
			j++;
		}
	}
	peerResults = validResults;
}

public long getFileSize()
{
	return fileSize;
}

public SearchReplyPeerEntry[] getPeerResults()
{
	return peerResults;
}

public MessageCode getMessageCode()
{
	return MessageCode.SearchReplyMessageCode;
}

public int getRawMessageLength()
{
	if (peerResults == null)
		return Message.HEADER_LENGTH + Message.FILESIZE_FIELD_WIDTH;
	
	return Message.HEADER_LENGTH + Message.FILESIZE_FIELD_WIDTH + (peerResults.length * PEER_ENTRY_WIDTH);
}

public ByteBuffer getRawMessage()
{
	// Create a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putInt(this.getRawMessageLength());
	
	// Write the size of the file
	rawMessage.putInt((int)fileSize);
	
	// Write the peer result entries
	if (peerResults != null)
	{
		for (int i = 0; i < peerResults.length; i++)
		{
			rawMessage.put(peerResults[i].getRawEntry());
		}
	}
	
	return rawMessage;
}

}
