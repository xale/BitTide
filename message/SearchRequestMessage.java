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

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
