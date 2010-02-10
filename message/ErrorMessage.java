package message;

public class ErrorMessage extends Message
{
	private static byte ErrorMessageCode = 8;

public byte getMessageCode()
{
	return ErrorMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
