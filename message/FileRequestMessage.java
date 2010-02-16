package message;

public class FileRequestMessage extends Message
{

public MessageCode getMessageCode()
{
	return MessageCode.FileRequestMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
