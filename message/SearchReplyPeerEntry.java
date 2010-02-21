package message;

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

}
