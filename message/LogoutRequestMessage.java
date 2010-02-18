package message;

import java.nio.*;

public class LogoutRequestMessage extends Message
{

public LogoutRequestMessage(ByteBuffer contents)
{
	// FIXME: WRITEME
}

public MessageCode getMessageCode()
{
	return MessageCode.LogoutRequestMessageCode;
}

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
