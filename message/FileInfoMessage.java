package message;

public class FileInfoMessage extends Message
{
	private static byte FileInfoMessageCode = 5;
	
public byte getMessageCode()
{
	return FileInfoMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
