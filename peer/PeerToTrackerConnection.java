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

public Message nextMessage()
	throws EOFException, IOException, ErrorMessageException
{
	// Read the next message from the input stream
	Message message = Message.nextMessageFromStream(readStream);
	
	// If the message is an error, throw an exception
	if (message.getMessageCode() == MessageCode.ErrorMessageCode)
	{
		ErrorMessage errorMessage = (ErrorMessage)message;
		throw new ErrorMessageException(errorMessage.getErrorDescription());
	}
	
	return message;
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