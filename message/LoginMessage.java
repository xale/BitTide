package message;

import java.io.*;
import java.nio.*;

public class LoginMessage extends Message
{
	private static final int PASSWORD_LENGTH =	Message.PASSWORD_FIELD_WIDTH;
	
	private int listenPort;
	private String username;
	private String password;

public LoginMessage(int peerListenPort, String peerName, String peerPass)
	throws IllegalArgumentException
{
	// Check the length of the password (assuming one-byte characters; no unicode support)
	if (peerPass.length() != PASSWORD_LENGTH)
	{
		throw new IllegalArgumentException("password must be four bytes in length");
	}
	
	listenPort = peerListenPort;
	username = peerName;
	password = peerPass;
}

public LoginMessage(ByteBuffer contents)
{
	// Read the peer's listen port
	listenPort = ByteBufferUtils.getUnsignedShortFrom(contents);
	
	// Read the peer's username
	int nameLength = contents.array().length - (Message.PORT_FIELD_WIDTH + Message.PASSWORD_FIELD_WIDTH);
	byte[] usernameBuffer = new byte[nameLength];
	contents.get(usernameBuffer);
	
	// Read the password
	byte[] passwordBuffer = new byte[Message.PASSWORD_FIELD_WIDTH];
	contents.get(passwordBuffer);
	
	// Convert username and password to strings
	try
	{
		username = new String(usernameBuffer, "ASCII");
		password = new String(passwordBuffer, "ASCII");
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in LoginMessage(ByteBuffer)");
	}
}

public int getListenPort()
{
	return listenPort;
}

public String getUsername()
{
	return username;
}

public String getPassword()
{
	return password;
}

public MessageCode getMessageCode()
{
	return MessageCode.LoginMessageCode;
}

public int getRawMessageLength()
{
	return (Message.HEADER_LENGTH + Message.PORT_FIELD_WIDTH + username.length() + Message.PASSWORD_FIELD_WIDTH);
}

public ByteBuffer getRawMessage()
{
	// Create a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putInt(this.getRawMessageLength());
	
	// Write the listen port
	rawMessage.putShort((short)listenPort);
	
	// Write the username and password
	try
	{
		rawMessage.put(username.getBytes("ASCII"));
		rawMessage.put(password.getBytes("ASCII"));
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in LoginMessage.getRawMessage()");
		
		return null;
	}
	
	return rawMessage;
}

}
