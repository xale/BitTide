package message;

public class FileInfoMessage extends Message
{
	private static byte FileInfoMessageCode = 1;
	
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
