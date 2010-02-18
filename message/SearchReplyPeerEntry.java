package message;

import java.net.*;
import java.util.*;

public class SearchReplyPeerEntry
{
	private InetSocketAddress address;
	private BitSet bitmap;

public SearchReplyPeerEntry(InetSocketAddress ipAddress, BitSet fileBitmap)
{
	address = ipAddress;
	bitmap = fileBitmap;
}

public SearchReplyPeerEntry(byte[] ipAddress, int port, byte[] fileBitmap)
	throws UnknownHostException
{
	// Create a socket address from the ip and port
	address = new InetSocketAddress(InetAddress.getByAddress(ipAddress), port);
	
	// Convert the bitmap
	bitmap = new BitSet(fileBitmap.length * Byte.SIZE);
	for (int i = 0; i < bitmap.size(); i++)
	{
		bitmap.set(i, ((fileBitmap[(Byte.SIZE * i)] & (1 << (i % Byte.SIZE))) != 0));
	}
}

public InetSocketAddress getAddress()
{
	return address;
}

public BitSet getFileBitmap()
{
	return bitmap;
}

}
