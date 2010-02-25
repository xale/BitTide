package peer;

import java.net.*;
import java.util.*;
import java.io.*;

public class IncomingPeerConnection implements Runnable
{
	private Socket peerSocket = null;
	private File downloadsDirectory = null;
	
public IncomingPeerConnection(Socket connectionSocket, File downloadsDir)
{
	peerSocket = connectionSocket;
	downloadsDirectory = downloadsDir;
}

public void run()
{
	InputStream readStream;
	OutputStream writeStream;
	
	try
	{
		// Open streams to peer
		readStream = peerSocket.getInputStream();
		writeStream = peerSocket.getOutputStream();
	}
	catch (Exception E)
	{
		// Failed to open streams; abort
		return;
	}

	try
	{
		// FIXME: WRITEME: handle connection
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	
	try
	{
		// Close write stream
		writeStream.close();
	}
	catch (IOException IOE)
	{
		// Do nothing...
	}
	
	try
	{
		// Close read stream
		readStream.close();
	}
	catch (IOException IOE)
	{
		// Do nothing...
	}
	
	try
	{
		// Close socket
		peerSocket.close();
	}
	catch (IOException IOE)
	{
		// Do nothing...
	}
}

}