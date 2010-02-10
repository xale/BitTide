package message;

public class LogoutRequestMessage extends Message
{
	private static byte LogoutRequestMessageCode = 4;
	
public byte getMessageCode()
{
	return LogoutRequestMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
