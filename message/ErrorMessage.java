package message;

public class ErrorMessage extends Message
{
	private static final String unspecifiedErrorMessage = "the tracker reported an unknown error";
	
	private String errorDescription;

public ErrorMessage(byte[] messagePayload)
{
	if (messagePayload.length == 0)
		errorDescription = unspecifiedErrorMessage;
	
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
