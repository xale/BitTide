package message;

import java.io.*;
import java.nio.*;

public class LoginMessage extends Message
{
	private static final int PORT_FIELD_WIDTH = Short.SIZE;			// Two bytes
	private static final int PASSWORD_FIELD_WIDTH = Integer.SIZE;	// Four bytes
	private static final long MAX_PASSWORD_VALUE = (0xFFFFFFFFL);
	
	private int listenPort;
	private String username;
	private long password;

public LoginMessage(int peerListenPort, String peerName, String peerPass)
	throws IllegalArgumentException
{
	try
	{
		// Attempt to parse the password as a long
		password = Long.parseLong(peerPass);
		
		// Check that the password is a valid size
		if ((password < 0) || (password > MAX_PASSWORD_VALUE))
			throw new NumberFormatException();
	}
	catch (NumberFormatException NFE)
	{
		throw new IllegalArgumentException("password must be an integer between 0 and " + MAX_PASSWORD_VALUE + ", inclusive");
	}
	
	listenPort = peerListenPort;
	username = username;
	password = password;
}

public LoginMessage(ByteBuffer contents)
{
	// Read the peer's listen port
	listenPort = (int)(contents.getShort() & 0x0000FFFF);
	
	// Read the peer's username
	byte[] usernameBuffer = new byte[(contents.array().length - (PORT_FIELD_WIDTH + PASSWORD_FIELD_WIDTH))];
	contents.get(usernameBuffer);
	
	// Convert to a string
	try
	{
		username = new String(usernameBuffer, "ASCII");
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("unsupported encoding exception caught in LoginMessage(ByteBuffer)");
	}
	
	// Read the password
	password = (long)(contents.getInt() & 0xFFFFFFFFL);
}

public int getListenPort()
{
	return listenPort;
}

public String getUsername()
{
	return username;
}

public long getPassword()
{
	return password;
}

public MessageCode getMessageCode()
{
	return MessageCode.LoginMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
