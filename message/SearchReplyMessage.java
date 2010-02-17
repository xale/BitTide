package message;

import java.nio.*;

public class SearchReplyMessage extends Message
{

public SearchReplyMessage(ByteBuffer contents)
{
	// FIXME: WRITEME
}

public MessageCode getMessageCode()
{
	return MessageCode.SearchReplyMessageCode;
}

public byte[] getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
