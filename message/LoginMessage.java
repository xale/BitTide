package message;

import java.io.*;
import java.nio.*;

public class LoginMessage extends Message
{
	private static final int PORT_FIELD_WIDTH =		2;
	private static final int PASSWORD_FIELD_WIDTH =	4;
	private static final int MAX_PASSWORD_LENGTH =	PASSWORD_FIELD_WIDTH;
	
	private int listenPort;
	private String username;
	private String password;

public LoginMessage(int peerListenPort, String peerName, String peerPass)
	throws IllegalArgumentException
{
	// Check the length of the password (assuming one-byte characters; no unicode support)
	if (peerPass.length() > MAX_PASSWORD_LENGTH)
	{
		throw new IllegalArgumentException("password must be four bytes or less in length");
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
	byte[] usernameBuffer = new byte[(contents.array().length - (PORT_FIELD_WIDTH + PASSWORD_FIELD_WIDTH))];
	contents.get(usernameBuffer);
	
	// Read the password
	byte[] passwordBuffer = new byte[PASSWORD_FIELD_WIDTH];
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
	return (Message.HEADER_LENGTH + PORT_FIELD_WIDTH + username.length() + PASSWORD_FIELD_WIDTH);
}

public ByteBuffer getRawMessage()
{
	// Create a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putInt(this.getRawMessageLength());
	
	// Write the listen port
	rawMessage.putShort((short)this.getListenPort());
	
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
