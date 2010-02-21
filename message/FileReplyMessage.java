package message;

import java.nio.*;

public class FileReplyMessage extends Message
{
	private int blockIndex;
	private ByteBuffer blockContents;
	
public FileReplyMessage(ByteBuffer contents)
{
	// Read the index of this block
	blockIndex = ByteBufferUtils.getUnsignedShortFrom(contents);
	
	// Slice the rest of the message into a new buffer
	byte[] rawContents = new byte[contents.array().length - Message.BLOCKINDEX_FIELD_WIDTH];
	contents.get(rawContents);
	blockContents = ByteBuffer.wrap(rawContents);
}

public int getBlockIndex()
{
	return blockIndex;
}

public ByteBuffer getBlockContents()
{
	return blockContents;
}

public MessageCode getMessageCode()
{
	return MessageCode.FileReplyMessageCode;
}

public int getRawMessageLength()
{
	return Message.HEADER_LENGTH + Message.BLOCKINDEX_FIELD_WIDTH + blockContents.array().length;
}

public ByteBuffer getRawMessage()
{
	// Create a buffer
	ByteBuffer rawMessage = ByteBuffer.allocate(this.getRawMessageLength());
	
	// Write the message header
	rawMessage.put(this.getMessageCode().getCode());
	rawMessage.putInt(this.getRawMessageLength());
	
	// Write the block index
	rawMessage.putShort((short)blockIndex);
	
	// Write the block contents
	rawMessage.put(blockContents);
	
	return rawMessage;
}

}
