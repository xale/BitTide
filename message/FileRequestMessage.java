package message;

import java.nio.*;

public class FileRequestMessage extends Message
{

public FileRequestMessage(ByteBuffer contents)
{
	// FIXME: WRITEME
}

public MessageCode getMessageCode()
{
	return MessageCode.FileRequestMessageCode;
}

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
