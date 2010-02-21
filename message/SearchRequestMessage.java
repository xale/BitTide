package message;

import java.io.*;
import java.nio.*;

public class SearchRequestMessage extends Message
{
	private String filename;

public SearchRequestMessage(String nameOfFile)
	throws IllegalArgumentException
{
	// Check the length of the filename
	if (nameOfFile.length() > Message.MAX_FILENAME_LENGTH)
		throw new IllegalArgumentException("filename too long: " + nameOfFile);
	
	filename = nameOfFile;
}

public SearchRequestMessage(ByteBuffer contents)
{
	// The only contents are the filename; just read it out
	byte[] rawFilename = new byte[contents.array().length];
	contents.get(rawFilename);
	
	// Convert to a string
	try
	{
		filename = new String(rawFilename, "ASCII");
	}
	catch (UnsupportedEncodingException UEE)
	{
		System.err.println("warning: unsupported encoding exception caught in SearchRequestMessage(ByteBuffer)");
	}
}

public String getFilename()
{
	return filename;
}

public MessageCode getMessageCode()
{
	return MessageCode.SearchRequestMessageCode;
}

public int getRawMessageLength()
{
	return Message.HEADER_LENGTH + filename.length();
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
		System.err.println("warning: unsupported encoding exception caught in SearchRequestMessage.getRawMessage()");
		
		return null;
	}
	
	return rawMessage;
}

}
