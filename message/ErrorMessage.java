package message;

public class ErrorMessage extends Message
{

public MessageCode getMessageCode()
{
	return MessageCode.ErrorMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
