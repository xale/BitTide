package message;

public class SearchRequestMessage extends Message
{
	private static byte SearchRequestMessageCode = 3;
	
public byte getMessageCode()
{
	return SearchRequestMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
