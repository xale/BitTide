package message;

public class LogoutRequestMessage extends Message
{

public MessageCode getMessageCode()
{
	return MessageCode.LogoutRequestMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
