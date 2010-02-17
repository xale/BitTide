package message;

public class LoginMessage extends Message
{
	private int listenPort;
	private String username;
	private String password;

public LoginMessage(int peerListenPort, String peerName, String peerPass)
	throws IllegalArgumentException
{
	// Check the length of the password
	// We are naively assuming that each character is one byte; we can't handle unicode here
	if (password.length() > 4)
		throw new IllegalArgumentException("maximum password length is 4 characters");
	
	listenPort = peerListenPort;
	username = username;
	password = password;
}

public LoginMessage(byte[] messagePayload)
{
	// FIXME: WRITEME
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

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
