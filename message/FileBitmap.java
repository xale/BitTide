package message;

import java.nio.*;
import java.util.*;

public class FileBitmap extends BitSet
{
	// All bitmaps are 12 bytes long
	public static final int FILE_BITMAP_NUM_BYTES = 12;
	public static final int FILE_BITMAP_SIZE = FILE_BITMAP_NUM_BYTES * Byte.SIZE;
	
/**
* Creates a new FileBitmap using the contents of a byte array.
* @param bitmap the byte array containing the bitmap of the represented file
*/
public FileBitmap(byte[] bitmap)
{
	super(FILE_BITMAP_SIZE);
	
	// Check the length of the bitmap
	if (bitmap.length != FILE_BITMAP_NUM_BYTES)
		System.err.println("warning: FileBitmap(byte[]) called with byte array of invalid length");
	
	// Convert the byte array into a BitSet
	for (int i = 0; i < FILE_BITMAP_SIZE; i++)
	{
		this.set(i, ((bitmap[(Byte.SIZE * i)] & (1 << ((Byte.SIZE - 1) - (i % Byte.SIZE)))) != 0));
	}
}

/**
* Creates a new FileBitmap with an empty byte array.
*/
public FileBitmap()
{
	super(FILE_BITMAP_SIZE);
	
	// Convert the byte array into a BitSet
	for (int i = 0; i < FILE_BITMAP_SIZE; i++)
	{
		this.set(i, 0);
	}
}

public ByteBuffer getRawBitmap()
{
	// Create a byte array
	byte[] rawBitmap = new byte[FILE_BITMAP_NUM_BYTES];
	
	// Convert the BitSet
	for (int i = 0; i < FILE_BITMAP_SIZE; i++)
	{
		if (this.get(i))
			rawBitmap[(i / Byte.SIZE)] |= (1 << ((Byte.SIZE - 1) - (i % Byte.SIZE)));
	}
	
	// Wrap and return the byte array
	return ByteBuffer.wrap(rawBitmap);
}

}