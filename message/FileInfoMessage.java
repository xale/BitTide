package message;

import java.io.*;
import java.nio.*;
import java.util.*;

public class FileInfoMessage extends Message
{
	private String filename;
	private long fileSize;
	private FileBitmap fileBitmap;
	
public FileInfoMessage(String nameOfFile, long size, FileBitmap bitmap)
	throws IllegalArgumentException
{
	// Check the length of the filename
	if (nameOfFile.length() > Message.MAX_FILENAME_LENGTH)
		throw new IllegalArgumentException("filename too long: " + nameOfFile);
	
	filename = nameOfFile;
	fileSize = size;
	fileBitmap = bitmap;
}
	
public FileInfoMessage(ByteBuffer contents)
{
	// Read the file size
	fileSize = ByteBufferUtils.getUnsignedIntFrom(contents);
	
	// Read the bitmap
	byte[] bitmap = new byte[Message.BITMAP_FIELD_WIDTH];
	contents.get(bitmap);
	fileBitmap = new FileBitmap(bitmap);
	
	// Read the file name
	int nameLength = contents.array().length - (Message.FILESIZE_FIELD_WIDTH + Message.BITMAP_FIELD_WIDTH);
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
	rawMessage.putInt(this.getRawMessageLength());
	
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
		
		return null;
	}
	
	return rawMessage;
}

}
