package message;

import java.io.*;
import java.nio.*;
import java.util.*;

public class FileInfoMessage extends Message
{
	private static final int MAX_FILENAME_LENGTH =	255;
	private static final int FILESIZE_FIELD_WIDTH =	4;
	private static final int BITMAP_FIELD_WIDTH = 	FileBitmap.FILE_BITMAP_NUM_BYTES;
	
	private String filename;
	private long fileSize;
	private FileBitmap fileBitmap;
	
public FileInfoMessage(String name, long size, FileBitmap bitmap)
	throws IllegalArgumentException
{
	// Check the length of the filename
	if (name.length() > MAX_FILENAME_LENGTH)
		throw new IllegalArgumentException("filename too long: " + name);
	
	filename = name;
	fileSize = size;
	fileBitmap = bitmap;
}
	
public FileInfoMessage(ByteBuffer contents)
{
	// Read the file size
	fileSize = (contents.getInt() & 0xFFFFFFFFL);
	
	// Read the bitmap
	byte[] bitmap = new byte[BITMAP_FIELD_WIDTH];
	contents.get(bitmap);
	fileBitmap = new FileBitmap(bitmap);
	
	// Read the file name
	int nameLength = contents.array().length - (FILESIZE_FIELD_WIDTH + BITMAP_FIELD_WIDTH);
	byte[] rawFilename = new byte[nameLength];
	contents.get(rawFilename);
	
	// Convert the filename to a string
	try
	{
		filename = new String(rawFilename, "ASCII");
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in FileInfoMessage(ByteBuffer)");
	}
}

public String getFilename()
{
	return filename;
}

public long getFileSize()
{
	return fileSize;
}

public FileBitmap getFileBitmap()
{
	return fileBitmap;
}

public MessageCode getMessageCode()
{
	return MessageCode.FileInfoMessageCode;
}

public int getRawMessageLength()
{
	return Message.HEADER_LENGTH + FILESIZE_FIELD_WIDTH + BITMAP_FIELD_WIDTH + filename.length();
}

public ByteBuffer getRawMessage()
{
	// Create a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putLong((long)this.getRawMessageLength());
	
	// Write the file size
	rawMessage.putInt((int)this.getFileSize());
	
	// Write the bitmap
	rawMessage.put(this.getFileBitmap().getRawBitmap());
	
	// Write the filename
	try
	{
		rawMessage.put(filename.getBytes("ASCII"));
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in FileInfoMessage.getRawMessage()");
	}
	
	return null;
}

}
