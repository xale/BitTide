package message;

public class FileBitmapMessage extends Message
{
	private static byte FileBitmapMessageCode = 6;
	
public byte getMessageCode()
{
	return FileBitmapMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
