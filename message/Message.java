package message;

import java.nio.*;

public abstract class Message
{
	// Width (number of bytes) of various fields of messages
	// Message code (1 byte)
	public static final int CODE_FIELD_WIDTH =		1;
	// Message length (2 bytes, if present)
	public static final int LENGTH_FIELD_WIDTH =	4;
	// Total header length
	public static final int HEADER_LENGTH = 		CODE_FIELD_WIDTH + LENGTH_FIELD_WIDTH;
	
	// Maximum data payload
	public static final int MAX_DATA_SIZE = 		(16 * 1024);
	
	// Maximum length of a filename
	public static final int MAX_FILENAME_LENGTH =	255;
	
	// File-block index value
	public static final int BLOCK_INDEX_FIELD_WIDTH = 2;
	
	// Maximum message length; only MSG_FILE_REP can be this long
	public static final long MAX_MESSAGE_LENGTH =	HEADER_LENGTH + BLOCK_INDEX_FIELD_WIDTH + MAX_DATA_SIZE;

public abstract MessageCode getMessageCode();

public abstract int getRawMessageLength();

public abstract ByteBuffer getRawMessage();

}
