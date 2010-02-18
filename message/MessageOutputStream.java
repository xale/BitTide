package message;

import java.io.*;
import java.nio.*;

public class MessageOutputStream extends DataOutputStream
{

public MessageOutputStream(OutputStream out)
{
	super(out);
}

public void writeMessage(Message message)
{
	// FIXME: WRITEME
}

}