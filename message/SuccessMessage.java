package message;

public class SuccessMessage extends Message
{

public SuccessMessage()
{
	// No data fields on a success message
}

public MessageCode getMessageCode()
{
	return MessageCode.SuccessMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
