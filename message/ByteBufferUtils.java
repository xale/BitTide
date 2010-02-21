package message;

import java.nio.*;

public abstract class ByteBufferUtils
{

public static short getUnsignedByteFrom(ByteBuffer bb)
{
	return (bb.get() & (short)0x00FF);
}

public static int getUnsignedShortFrom(ByteBuffer bb)
{
	return (bb.getShort() & 0x0000FFFF);
}

public static long getUnsignedShortFrom(ByteBuffer bb)
{
	return (bb.getInt() & 0xFFFFFFFFL);
}

}
