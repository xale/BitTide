package peer;

import java.net.*;
import java.io.*;
import message.*;

public class PeerToTrackerConnection
{
	private InetSocketAddress address = null;
	private Socket trackerSocket = null;
	private MessageInputStream readStream = null;
	private MessageOutputStream writeStream = null;
	
public PeerToTrackerConnection(InetSocketAddress trackerAddress)
{
	address = trackerAddress;
}

public InetSocketAddress getAddress()
{
	return address;
}

public void open()
	throws IOException
{
	// Attempt to connect a socket to the tracker
	trackerSocket = new Socket(address.getAddress(), address.getPort());
	
	// Open streams on the socket
	readStream = new MessageInputStream(trackerSocket.getInputStream());
	writeStream = new MessageOutputStream(trackerSocket.getOutputStream());
}

public Message sendMessage(Message message)
	throws EOFException, ErrorMessageException, IOException
{
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

public boolean isClosed()
{
	return (trackerSocket == null) || (trackerSocket.isClosed());
}

public void close()
	throws IOException
{
	// Close the socket (automatically closes the streams)
	trackerSocket.close();
}

}