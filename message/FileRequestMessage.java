package message;

public class FileRequestMessage extends Message
{
	private static byte FileRequestMessageCode = 9;
	
public byte getMessageCode()
{
	return FileRequestMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
