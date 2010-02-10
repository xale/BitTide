package message;

public class FileReplyMessage extends Message
{
	private static byte FileReplyMessageCode = 10;
	
public byte getMessageCode()
{
	return FileReplyMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
