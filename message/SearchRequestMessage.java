package message;

import java.nio.*;

public class SearchRequestMessage extends Message
{

public SearchRequestMessage(ByteBuffer contents)
{
	// FIXME: WRITEME
}

public MessageCode getMessageCode()
{
	return MessageCode.SearchRequestMessageCode;
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
