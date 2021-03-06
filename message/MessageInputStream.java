package message;

import java.io.*;
import java.nio.*;

public class MessageInputStream extends DataInputStream
{

public MessageInputStream(InputStream in)
{
	super(in);
}

/**
*	Blocks on reading the stream until a message can be constructed fully, then returns a message of the appropriate type when it has received enough information.
*	@return the next message on the stream, encapsulated in the appropriate Message subclass
*	@throws EOFException if an end-of-file is encountered before the message can be constructed fully
*	@throws IOException if reading from the stream fails, if an unknown message code is received, or if the length field of the message is greater than the maximum possible message length
*	@throws ErrorMessageException if the message received is an ErrorMessage; the exception message will be the error message description
*/
public Message readMessage()
	throws IOException, EOFException, ErrorMessageException
{
	// Read the first byte of the message
	byte firstByte = super.readByte();
	
	// Determine the message type
	MessageCode messageCode = MessageCode.messageCodeFromFirstByte(firstByte);
	
	// Check if the code is valid
	if (messageCode == MessageCode.InvalidMessageCode)
		throw new IOException("invalid message code received: " + firstByte);
	
	// If this is a simple "success" message, just return it
	if (messageCode == MessageCode.SuccessMessageCode)
		return new SuccessMessage();
	
	// Otherwise, read the length field as an unsigned int
	long messageLength = this.readUnsignedInt();
	
	// Check if the payload is longer than the maximum message length
	if (messageLength > Message.MAX_MESSAGE_LENGTH)
		throw new IOException("message of " + messageLength + " bytes is too long");
	
	// Subtract the size of the header from the message length
	int payloadLength = (int)(messageLength - Message.HEADER_LENGTH);
	
	// If the payload length is non-zero, read the payload into a buffer
	ByteBuffer messagePayload = null;
	if (payloadLength > 0)
	{
		messagePayload = ByteBuffer.wrap(new byte[payloadLength]);
		super.readFully(messagePayload.array());
	}
	
	// Create and return the appropriate type of message, containing the payload contents
	switch (messageCode)
	{
		case LoginMessageCode:
			return new LoginMessage(messagePayload);
			
		case SearchRequestMessageCode:
			return new SearchRequestMessage(messagePayload);
			
		case SearchReplyMessageCode:
			return new SearchReplyMessage(messagePayload);
			
		case FileInfoMessageCode:
			return new FileInfoMessage(messagePayload);
			
		case FileBitmapMessageCode:
			return new FileBitmapMessage(messagePayload);
			
		case LogoutRequestMessageCode:
			return new LogoutRequestMessage(messagePayload);
			
		case ErrorMessageCode:
			throw new ErrorMessageException(new ErrorMessage(messagePayload));
			
		case FileRequestMessageCode:
			return new FileRequestMessage(messagePayload);
			
		case FileReplyMessageCode:
			return new FileReplyMessage(messagePayload);
			
		case LogoutCompleteMessageCode:
			return new LogoutCompleteMessage(messagePayload);
	}
	
	// Execution should never reach this point; an exception should have been thrown earlier if the message code was not a known code
	
	return null;
}

public long readUnsignedInt()
	throws IOException
{
	return (super.readInt() & 0xFFFFFFFFL);
}

}