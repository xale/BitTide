package message;

import java.net.*;
import java.util.*;

public class SearchReplyPeerEntry
{
	private InetSocketAddress address;
	private BitSet fileBitmap;

public SearchReplyPeerEntry(byte[] ipAddress, int port, byte[] bitmap)
	throws UnknownHostException
{
	// Create a socket address from the ip and port
	address = new InetSocketAddress(InetAddress.getByAddress(ipAddress), port);
	
	// Convert the bitmap
	fileBitmap = new BitSet(bitmap.length * Byte.SIZE);
	for (int i = 0; i < fileBitmap.size(); i++)
	{
		fileBitmap.set(i, ((bitmap[(Byte.SIZE * i)] & (1 << (i % Byte.SIZE))) != 0));
	}
}

public InetSocketAddress getAddress()
{
	return address;
}

public BitSet getFileBitmap()
{
	return fileBitmap;
}

}
