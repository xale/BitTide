package message;

public class ErrorMessage extends Message
{
	private String errorDescription;

public ErrorMessage(byte[] messagePayload)
{
	// FIXME: WRITEME
}

public String getErrorDescription()
{
	return errorDescription;
}

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
