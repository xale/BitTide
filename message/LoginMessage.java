package message;

public class LoginMessage extends Message
{
	private static byte LoginMessageCode = 2;
	
	private int listenPort;
	private byte[] username;
	private byte[] password;

public LoginMessage(int peerListenPort, String peerName, String peerPass)
	throws IllegalArgumentException
{
	listenPort = peerListenPort;
	
	// Attempt to convert the username and password into ASCII byte arrays
	/* FIXME: WRITEME
	try
	{
		username = peerName.getBytes("US-ASCII");
		password = peerPass.getBytes("US-ASCII");
	}
	*/
	
	// FIXME: temporary
	username = null;
	password = null;
}

public int getListenPort()
{
	return listenPort;
}

public byte[] getUsername()
{
	return username;
}

public byte[] getPassword()
{
	return password;
}

public byte getMessageCode()
{
	return LoginMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
