package message;

import java.nio.*;

public abstract class Message
{
	public static final int HEADER_LENGTH = 5;
	public static final long MAX_MESSAGE_LENGTH = ((16 * 1024) + 2) + HEADER_LENGTH;

public abstract MessageCode getMessageCode();

public abstract ByteBuffer getRawMessage();

}
