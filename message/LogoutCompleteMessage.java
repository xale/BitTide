package message;

import java.nio.*;

public class LogoutCompleteMessage extends Message
{

public LogoutCompleteMessage(ByteBuffer contents)
{
	// FIXME: WRITEME
}

public MessageCode getMessageCode()
{
	return MessageCode.LogoutCompleteMessageCode;
}

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
