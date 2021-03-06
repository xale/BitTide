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
	
	// IP Address
	public static final int IP_FIELD_WIDTH =		4;
	// Port
	public static final int PORT_FIELD_WIDTH =		2;
	
	// Peer's password
	public static final int PASSWORD_FIELD_WIDTH =	4;
	
	// Maximum length of a filename
	public static final int MAX_FILENAME_LENGTH =	255;
	// File size
	public static final int FILESIZE_FIELD_WIDTH =	4;
	// File-bitmap
	public static final int BITMAP_FIELD_WIDTH = 	FileBitmap.FILE_BITMAP_NUM_BYTES;
	// File-block index value
	public static final int BLOCKINDEX_FIELD_WIDTH = 2;
	// File-block data size
	public static final int MAX_BLOCK_SIZE =		(16 * 1024);
	
	// Width of an entry in a MSG_SEARCH_REP
	public static final int PEER_ENTRY_WIDTH = IP_FIELD_WIDTH + PORT_FIELD_WIDTH + BITMAP_FIELD_WIDTH;
	
	// Maximum message length; only MSG_FILE_REP can be this long
	public static final long MAX_MESSAGE_LENGTH =	HEADER_LENGTH + BLOCKINDEX_FIELD_WIDTH + MAX_BLOCK_SIZE;

public abstract MessageCode getMessageCode();

public abstract int getRawMessageLength();

public abstract ByteBuffer getRawMessage();

}
