package peer;

import java.net.*;
import java.io.*;
import message.*;

public class PeerToTrackerConnection
{
	private Socket trackerSocket = null;
	private DataInputStream readStream = null;
	private DataOutputStream writeStream = null;
	
public PeerToTrackerConnection(InetSocketAddress trackerAddress)
	throws UnknownHostException, IOException
{
	// Connect a socket to the specified address
	trackerSocket = new Socket(trackerAddress.getAddress(), trackerAddress.getPort());
	
	// Open streams on the socket
	writeStream = new DataOutputStream(trackerSocket.getOutputStream());
	readStream = new DataInputStream(trackerSocket.getInputStream());
}

public void sendMessage(Message message)
	throws IOException
{
	// FIXME: WRITEME
}

private static final String unspecifiedErrorMessage = "an unknown error occurred";

public Message nextMessage()
	throws EOFException, IOException, ErrorMessageException
{
	// Read and return the next message from the input stream
	//return MessageFactory.nextMessageFromStream(readStream);
	
	// FIXME: temporary
	return null;
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