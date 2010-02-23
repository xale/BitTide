package message;

import java.io.*;
import java.nio.*;

public class MessageOutputStream extends DataOutputStream
{

public MessageOutputStream(OutputStream out)
{
	super(out);
}

public void writeMessage(Message message)
	throws IOException
{
	// Get the raw representation of the message
	byte[] rawMessage = new byte[message.getRawMessageLength()];
	message.getRawMessage().get(rawMessage);
	
	// Write the message to the output stream
	super.write(rawMessage);
}

}