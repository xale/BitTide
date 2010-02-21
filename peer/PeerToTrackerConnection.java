package peer;

import java.net.*;
import java.io.*;
import message.*;

public class PeerToTrackerConnection
{
	private InetSocketAddress address;
	
public PeerToTrackerConnection(InetSocketAddress trackerAddress)
{
	address = trackerAddress;
}

public Message sendMessage(Message message)
	throws EOFException, ErrorMessageException, IOException
{
	Socket trackerSocket = null;
	MessageInputStream readStream = null;
	MessageOutputStream writeStream = null;
	
	try
	{
		// Connect a socket to the specified address
		trackerSocket = new Socket(address.getAddress(), address.getPort());
		
		// Open streams on the socket
		writeStream = new MessageOutputStream(trackerSocket.getOutputStream());
		readStream = new MessageInputStream(trackerSocket.getInputStream());
		
		// Send the outgoing message
		writeStream.writeMessage(message);
		
		// Read the tracker's reply from the input stream
		Message replyMessage = readStream.readMessage();
		
		// If the message is an error, throw an exception
		if (replyMessage.getMessageCode() == MessageCode.ErrorMessageCode)
			throw new ErrorMessageException((ErrorMessage)replyMessage);
		
		// Otherwise, return the reply message
		return replyMessage;
	}
	finally
	{
		// Close sockets before exiting
		if ((trackerSocket != null) && !trackerSocket.isClosed())
			trackerSocket.close();
	}
}

public InetSocketAddress getAddress()
{
	return address;
}

}