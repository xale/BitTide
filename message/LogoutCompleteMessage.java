package message;

public class LogoutCompleteMessage extends Message
{
	private static byte LogoutCompleteMessageCode = 11;
	
public byte getMessageCode()
{
	return LogoutCompleteMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
