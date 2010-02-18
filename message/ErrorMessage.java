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
	this(unspecifiedErrorDescription);
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
			System.err.println("unsupported encoding exception caught in ErrorMessage(ByteBuffer)");
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

public ByteBuffer getRawMessage()
{
	// FIXME: WRITEME
	return null;
}

}
