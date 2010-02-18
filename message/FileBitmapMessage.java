package message;

import java.nio.*;

public class FileBitmapMessage extends Message
{

public FileBitmapMessage(ByteBuffer contents)
{
	// FIXME: WRITEME
}

public MessageCode getMessageCode()
{
	return MessageCode.FileBitmapMessageCode;
}

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
