package message;

public class LogoutRequestMessage extends Message
{

public LogoutRequestMessage(byte[] messagePayload)
{
	// FIXME: WRITEME
}

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
