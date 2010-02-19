package message;

import java.net.*;
import java.nio.*;
import java.util.*;

public class SearchReplyMessage extends Message
{
	private static final int SIZE_FIELD_WIDTH =		(Integer.SIZE / Byte.SIZE);	// Four bytes
	
	private static final int IP_FIELD_WIDTH =		(Integer.SIZE / Byte.SIZE);	// Four bytes
	private static final int PORT_FIELD_WIDTH =		(Short.SIZE / Byte.SIZE);	// Two bytes
	private static final int BITMAP_FIELD_WIDTH = 	12;
	private static final int PEER_ENTRY_WIDTH = IP_FIELD_WIDTH + PORT_FIELD_WIDTH + BITMAP_FIELD_WIDTH;
	
	private long fileSize;
	private SearchReplyPeerEntry[] peerResults;

public SearchReplyMessage(long sizeOfFile, SearchReplyPeerEntry[] peers)
{
	fileSize = sizeOfFile;
	peerResults = peers;
}

public SearchReplyMessage(ByteBuffer contents)
{
	// Retrieve the size of the file
	fileSize = (contents.getInt() & 0xFFFFFFFFL);
	
	// Determine the number of peers returned in the search result
	int numPeers = ((contents.array().length - SIZE_FIELD_WIDTH) / PEER_ENTRY_WIDTH);
	
	// Parse the entries
	peerResults = new SearchReplyPeerEntry[numPeers];
	int numValidResults = 0;
	for (int i = 0; i < numPeers; i++)
	{
		try
		{
			// Read the peer's ip address
			byte[] ip = new byte[IP_FIELD_WIDTH];
			contents.get(ip);
			
			// Read the peer's listening port
			int port = (contents.getShort() & 0x0000FFFF);
			
			// Read the peer's bitmap of the file
			byte[] fileBitmap = new byte[BITMAP_FIELD_WIDTH];
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
	SearchReplyPeerEntry validResults = new SearchReplyPeerEntry[numValidResults];
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

public long getRawMessageLength()
{
	return (Message.HEADER_LENGTH + SIZE_FIELD_WIDTH + (peerResults.length * PEER_ENTRY_WIDTH));
}

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
