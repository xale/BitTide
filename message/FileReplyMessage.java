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

public int getRawMessageLength()
{
	// FIXME: WRITEME
	return 0;
}

public ByteBuffer getRawMessage()
{
	// Create a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putLong((long)this.getRawMessageLength());
	
	// FIXME: WRITEME
	
	return null;
}

}
