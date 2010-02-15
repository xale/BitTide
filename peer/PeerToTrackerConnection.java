package peer;

import java.net.*;
import java.util.*;
import java.io.*;
import message.*;

public class PeerToTrackerConnection
{
	private Socket trackerSocket = null;
	private InputStream readStream = null;
	private OutputStream writeStream = null;
	
public PeerToTrackerConnection(InetSocketAddress trackerAddress)
	throws UnknownHostException, IOException
{
	// Connect a socket to the specified address
	trackerSocket = new Socket(trackerAddress.getAddress(), trackerAddress.getPort());
	
	// Open streams on the socket
	writeStream = trackerSocket.getOutputStream();
	readStream = trackerSocket.getInputStream();
}

public void close()
	throws IOException
{
	// Close the socket
	trackerSocket.close();
}

public boolean isClosed()
{
	return trackerSocket.isClosed();
}

}