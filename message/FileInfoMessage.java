package message;

public class FileInfoMessage extends Message
{

public MessageCode getMessageCode()
{
	return MessageCode.FileInfoMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
