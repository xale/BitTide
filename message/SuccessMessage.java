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

public int getRawMessageLength()
{
	return Message.CODE_FIELD_WIDTH;
}

public ByteBuffer getRawMessage()
{
	return ByteBuffer.allocate(this.getRawMessageLength()).put(this.getMessageCode().getCode());
}

}
