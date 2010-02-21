package message;

import java.nio.*;

public abstract class ByteBufferUtils
{

public static int getUnsignedShortFrom(ByteBuffer bb)
{
	return (bb.getShort() & 0x0000FFFF);
}

public static long getUnsignedIntFrom(ByteBuffer bb)
{
	return (bb.getInt() & 0xFFFFFFFFL);
}

}
