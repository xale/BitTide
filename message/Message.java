package message;

import java.nio.*;

public abstract class Message
{
	public static final int CODE_FIELD_WIDTH = (Byte.SIZE / Byte.SIZE);
	public static final int LENGTH_FIELD_WIDTH = (Integer.SIZE / Byte.SIZE);
	public static final int HEADER_LENGTH = CODE_FIELD_WIDTH + LENGTH_FIELD_WIDTH;
	public static final long MAX_MESSAGE_LENGTH = ((16 * 1024) + 2) + HEADER_LENGTH;

public abstract MessageCode getMessageCode();

public abstract long getRawMessageLength();

public abstract ByteBuffer getRawMessage();

}
