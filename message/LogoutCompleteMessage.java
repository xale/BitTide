package message;

public class LogoutCompleteMessage extends Message
{

public MessageCode getMessageCode()
{
	return MessageCode.LogoutCompleteMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
