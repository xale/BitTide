package message;

public class SearchRequestMessage extends Message
{

public SearchRequestMessage(byte[] messagePayload)
{
	// FIXME: WRITEME
}

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
