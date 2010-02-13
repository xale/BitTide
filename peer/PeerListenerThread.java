package peer;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class PeerListenerThread extends Thread
{
	private ServerSocket listenSocket = null;
	private boolean listening; 
	
	private static final int SOCKET_ACCEPT_TIMEOUT = 1000; // milliseconds

public PeerListenerThread()
	throws IOException
{
	super("PeerListener");
	listenSocket = new ServerSocket(0); // binds to any open port
	listenSocket.setSoTimeout(SOCKET_ACCEPT_TIMEOUT);
}

public void run()
{
	// Create a single-thread pool
	ExecutorService threadPool = Executors.newSingleThreadExecutor();
	
	// Start listening for incoming connections
	this.setListening(true);
	while (this.isListening())
	{
		try
		{
			// For each incoming request, create and enqueue a job on the thread pool
			threadPool.execute(new PeerConnection(this.getListenSocket().accept()));
		}
		catch (SocketTimeoutException STE)
		{
			// Ignore; the connection just times out periodically to check if the thread should exit
		}
		catch (Exception E)
		{
			// Abort thread
			this.setListening(false);
		}
	}
	
	// Stop accepting new tasks to the thread pool
	threadPool.shutdown();
	
	try
	{
		// If the current task doesn't finish in ten seconds, force termination
		if (!threadPool.awaitTermination(10, TimeUnit.SECONDS))
			threadPool.shutdownNow();
	}
	catch (InterruptedException E)
	{
		// If the wait is interrupted, force termination immediately
		threadPool.shutdownNow();
	}
	
	try
	{
		// Close the peer-listening socket
		listenSocket.close();
	}
	catch (Exception E)
	{
		E.printStackTrace();
	}
}

public synchronized void setListening(boolean isListening)
{
	listening = isListening;
}

public synchronized boolean isListening()
{
	return listening;
}

public synchronized ServerSocket getListenSocket()
{
	return listenSocket;
}

}
