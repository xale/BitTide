package message;

import java.io.*;
import java.nio.*;

public class FileRequestMessage extends Message
{
	private String filename;
	private int beginBlockIndex;
	private int endBlockIndex;

/**
* Creates a new FileRequestMessage, asking a peer for a range of blocks from the specified file.
* @param nameOfFile the name of the file for which blocks are being requested
* @param startIndex the index of the first block being requested from the file
* @param endIndex the index of the last block being requested from the file
* @throws IllegalArgumentException if the filename is too long, or the start or end indexes are less than 0 or greater than the largest possible block index (95)
*/
public FileRequestMessage(String nameOfFile, int startIndex, int endIndex)
	throws IllegalArgumentException
{
	// Check the length of the filename
	if (nameOfFile.length() > Message.MAX_FILENAME_LENGTH)
		throw new IllegalArgumentException("filename too long: " + nameOfFile);
	
	// Check that the start and end block indexes are valid
	if ((startIndex < 0) || (startIndex >= FileBitmap.FILE_BITMAP_SIZE))
		throw new IllegalArgumentException("invalid file block index: " + startIndex);
	if ((endIndex < 0) || (endIndex >= FileBitmap.FILE_BITMAP_SIZE))
		throw new IllegalArgumentException("invalid file block index: " + endIndex);
	
	filename = nameOfFile;
	beginBlockIndex = startIndex;
	endBlockIndex = endIndex;
}
	
public FileRequestMessage(ByteBuffer contents)
{
	// Read the filename
	int nameLength = contents.array().length - (Message.BLOCKINDEX_FIELD_WIDTH * 2);
	byte[] rawFilename = new byte[nameLength];
	contents.get(rawFilename);
	
	// Convert the filename to a string
	try
	{
		filename = new String(rawFilename, "ASCII");
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in FileRequestMessage(ByteBuffer)");
	}
	
	// Read the block indexes
	beginBlockIndex = ByteBufferUtils.getUnsignedShortFrom(contents);
	endBlockIndex = ByteBufferUtils.getUnsignedShortFrom(contents);
}

public String getFilename()
{
	return filename;
}

public int getBeginBlockIndex()
{
	return beginBlockIndex;
}

public int getEndBlockIndex()
{
	return endBlockIndex;
}

public MessageCode getMessageCode()
{
	return MessageCode.FileRequestMessageCode;
}

public int getRawMessageLength()
{
	return Message.HEADER_LENGTH + filename.length() + (Message.BLOCKINDEX_FIELD_WIDTH * 2);
}

public ByteBuffer getRawMessage()
{
	// Create a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putInt(this.getRawMessageLength());
	
	// Write the filename
	try
	{
		rawMessage.put(filename.getBytes("ASCII"));
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in FileRequestMessage.getRawMessage()");
		
		return null;
	}
	
	// Write the block indexes
	rawMessage.putShort((short)this.getBeginBlockIndex());
	rawMessage.putShort((short)this.getEndBlockIndex());
	
	return rawMessage;
}

}
