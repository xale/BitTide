package message;

public class SearchReplyMessage extends Message
{
	private static byte SearchReplyMessageCode = 4;
	
public byte getMessageCode()
{
	return SearchReplyMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
