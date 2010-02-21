package message;

import java.io.*;
import java.nio.*;

public class FileBitmapMessage extends Message
{
	private String filename;
	private FileBitmap fileBitmap;

/**
* Creates a new FileBitmap message, containing the name of the file and the current bitmap for that file.
* @param nameOfFile the name of the file the bitmap represents
* @param bitmap the current bitmap of the file
* @throws IllegalArgumentException if the filename is too long
*/
public FileBitmapMessage(String nameOfFile, FileBitmap bitmap)
	throws IllegalArgumentException
{
	// Check the length of the filename
	if (nameOfFile.length() > Message.MAX_FILENAME_LENGTH)
		throw new IllegalArgumentException("filename too long: " + nameOfFile);
	
	filename = nameOfFile;
	fileBitmap = bitmap;
}

public FileBitmapMessage(ByteBuffer contents)
{
	// Read the filename
	int nameLength = contents.array().length - Message.BITMAP_FIELD_WIDTH;
	byte[] rawFilename = new byte[nameLength];
	contents.get(rawFilename);
	
	// Convert the filename to a string
	try
	{
		filename = new String(rawFilename, "ASCII");
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in FileBitmapMessage(ByteBuffer)");
	}
	
	// Read the bitmap
	byte[] rawBitmap = new byte[Message.BITMAP_FIELD_WIDTH];
	contents.get(rawBitmap);
	fileBitmap = new FileBitmap(rawBitmap);
}

public String getFilename()
{
	return filename;
}

public FileBitmap getFileBitmap()
{
	return fileBitmap;
}

public MessageCode getMessageCode()
{
	return MessageCode.FileBitmapMessageCode;
}

public int getRawMessageLength()
{
	return Message.HEADER_LENGTH + filename.length() + Message.BITMAP_FIELD_WIDTH;
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
		System.err.println("warning: unsupported encoding exception caught in FileBitmapMessage.getRawMessage()");
		
		return null;
	}
	
	// Write the bitmap
	rawMessage.put(fileBitmap.getRawBitmap());
	
	return rawMessage;
}

}
