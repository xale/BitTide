package message;

import java.io.*;
import java.nio.*;

public class ErrorMessage extends Message
{
	private static final String unspecifiedErrorDescription = "the tracker reported an unknown error";
	
	private String errorDescription;

public ErrorMessage(String description)
{
	errorDescription = description;
}

public ErrorMessage()
{
	this("");
}

public ErrorMessage(ByteBuffer contents)
{
	// Check if the message contains an error description
	if (contents != null)
	{
		// Convert to a string
		try
		{
			errorDescription = new String(contents.array(), "ASCII");
		}
		catch (UnsupportedEncodingException UEE)
		{
			System.err.println("warning: unsupported encoding exception caught in ErrorMessage(ByteBuffer)");
		}
	}
	else
	{
		// Use the placeholder error description
		errorDescription = unspecifiedErrorDescription;
	}
}

public String getErrorDescription()
{
	return errorDescription;
}

public MessageCode getMessageCode()
{
	return MessageCode.ErrorMessageCode;
}

public int getRawMessageLength()
{
	if (errorDescription != null)
		return (Message.HEADER_LENGTH + errorDescription.length());
	
	return Message.HEADER_LENGTH;
}

public ByteBuffer getRawMessage()
{
	// Allocate a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putInt(this.getRawMessageLength());
	
	// Write the error description, if present
	if ((errorDescription != null) && (errorDescription.length() > 0))
	{
		try
		{
			rawMessage.put(errorDescription.getBytes("ASCII"));
		}
		catch (UnsupportedEncodingException UEE)
		{
			System.err.println("warning: unsupported encoding exception caught in ErrorMessage.getRawMessage()");
		}
	}
	
	return rawMessage;
}

}
