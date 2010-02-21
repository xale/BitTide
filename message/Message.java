package message;

import java.nio.*;

public abstract class Message
{
	public static final int CODE_FIELD_WIDTH =		1;
	public static final int LENGTH_FIELD_WIDTH =	4;
	
	public static final int HEADER_LENGTH = CODE_FIELD_WIDTH + LENGTH_FIELD_WIDTH;
	
	public static final long MAX_MESSAGE_LENGTH = ((16 * 1024) + 2) + HEADER_LENGTH;
	
	public static final int MAX_FILENAME_LENGTH = 255;
	
	public static final int BLOCK_INDEX_FIELD_WIDTH = 2;

public abstract MessageCode getMessageCode();

public abstract int getRawMessageLength();

public abstract ByteBuffer getRawMessage();

}
