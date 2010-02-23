package message;

import java.io.*;
import java.nio.*;

public class LogoutRequestMessage extends Message
{
	private String username;

public LogoutRequestMessage(String peerName)
{
	username = peerName;
}

public LogoutRequestMessage(ByteBuffer contents)
{
	// Read the username
	int nameLength = contents.array().length - (Message.PORT_FIELD_WIDTH + Message.PASSWORD_FIELD_WIDTH);
	byte[] rawName = new byte[nameLength];
	contents.get(rawName);
	
	// Convert to a string
	try
	{
		username = new String(rawName, "ASCII");
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in LogoutRequestMessage(ByteBuffer)");
	}
}

public String getUsername()
{
	return username;
}

public MessageCode getMessageCode()
{
	return MessageCode.LogoutRequestMessageCode;
}

public int getRawMessageLength()
{
	return Message.HEADER_LENGTH + username.length();
}

public ByteBuffer getRawMessage()
{
	// Create a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putInt(this.getRawMessageLength());
	
	// Write the username
	try
	{
		rawMessage.put(username.getBytes("ASCII"));
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in LogoutRequestMessage.getRawMessage()");
		
		return null;
	}
	
	return rawMessage;
}

}
