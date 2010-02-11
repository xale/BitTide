package message;

public class SuccessMessage extends Message
{
	private static byte SuccessMessageCode = 1;
	
public byte getMessageCode()
{
	return SuccessMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
