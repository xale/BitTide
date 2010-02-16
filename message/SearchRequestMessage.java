package message;

public class SearchRequestMessage extends Message
{
	
public MessageCode getMessageCode()
{
	return MessageCode.SearchRequestMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
