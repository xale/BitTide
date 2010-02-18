package message;

import java.nio.*;

public class SuccessMessage extends Message
{

public SuccessMessage()
{
	// No data fields on a success message
}

public MessageCode getMessageCode()
{
	return MessageCode.SuccessMessageCode;
}

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
