package message;

import java.nio.*;
import java.net.*;
import java.util.*;

public class SearchReplyPeerEntry
{
	private InetSocketAddress address;
	private FileBitmap fileBitmap;

public SearchReplyPeerEntry(InetSocketAddress ipAddress, FileBitmap bitmap)
{
	address = ipAddress;
	fileBitmap = bitmap;
}

public SearchReplyPeerEntry(byte[] ipAddress, int port, byte[] bitmap)
	throws UnknownHostException
{
	// Create a socket address from the ip and port
	address = new InetSocketAddress(InetAddress.getByAddress(ipAddress), port);
	
	// Create the file bitmap
	fileBitmap = new FileBitmap(bitmap);
}

public InetSocketAddress getAddress()
{
	return address;
}

public FileBitmap getFileBitmap()
{
	return fileBitmap;
}

public ByteBuffer getRawEntry()
{
	// Create a ByteBuffer
	ByteBuffer rawEntry = ByteBuffer.allocate(Message.PEER_ENTRY_WIDTH);
	
	// Write the IP addres
	rawEntry.put(getAddress().getAddress().getAddress()); // lol
	
	// Write the port number
	rawEntry.putShort((short)address.getPort());
	
	// Write the bitmap
	rawEntry.put(fileBitmap.getRawBitmap());
	
	return rawEntry;
}

}
