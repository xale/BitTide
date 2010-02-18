package message;

import java.nio.*;

public class FileInfoMessage extends Message
{

public FileInfoMessage(ByteBuffer contents)
{
	// FIXME: WRITEME
}

public MessageCode getMessageCode()
{
	return MessageCode.FileInfoMessageCode;
}

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
