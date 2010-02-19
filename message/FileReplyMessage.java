package message;

import java.nio.*;

public class FileReplyMessage extends Message
{

public FileReplyMessage(ByteBuffer contents)
{
	// FIXME: WRITEME
}

public MessageCode getMessageCode()
{
	return MessageCode.FileReplyMessageCode;
}

public long getRawMessageLength()
{
	// FIXME: WRITEME
	return 0;
}

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
