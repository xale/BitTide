package peer;

import java.net.*;
import java.util.*;
import java.io.*;
import message.*;

public class Peer
{
	private static InetSocketAddress trackerAddress = null;
	
	private static Socket trackerSocket = null;
	private static InputStream readStream = null;
	private static OutputStream writeStream = null;
	
	private static String username = null;
	private static String password = null;
	
	private static URI downloadsDirectory = null;
	
	private static PeerListenerThread peerListener = null;

public static void main(String[] args)
{
	// Attempt to parse arguments
	try
	{
		parseArguments(args);
	}
	catch (IllegalArgumentException badArgs)
	{
		usage();
		System.exit(1);
	}
	
	// Create a thread to listen for incoming peer connections
	try
	{
		System.out.print("Starting peer listener... ");
		peerListener = new PeerListenerThread();
		System.out.println("done");
	}
	catch (IOException E)
	{
		System.err.println("ERROR: could not start peer listener");
		closeSocketsAndExit(1);
	}
	
	// Attempt to connect to the server
	try
	{
		// Connect a socket to the specified address
		System.out.print("Connecting to tracker " + trackerAddress + "... ");
		trackerSocket = new Socket(trackerAddress.getAddress(), trackerAddress.getPort());
		System.out.println(" done");
		
		// Open streams on the socket
		System.out.print("Opening streams to tracker... ");
		writeStream = trackerSocket.getOutputStream();
		readStream = trackerSocket.getInputStream();
		System.out.println(" done");
	}
	catch (UnknownHostException UHE)
	{
		System.err.println("ERROR: cannot connect to host: " + trackerAddress.getAddress());
		closeSocketsAndExit(1);
	}
	catch (IOException E)
	{
		System.err.println("ERROR: cannot get streams to host " + trackerAddress.getAddress());
		closeSocketsAndExit(1);
	}
	
	// Send a login message
	// FIXME: WRITEME
	
	// Enter interactive loop
	// FIXME: WRITEME
	
	// Close everything and exit
	closeSocketsAndExit(0);
}

public static void parseArguments(String[] args)
	throws IllegalArgumentException
{
	try
	{
		// Attempt to parse the tracker address
		InetAddress ip = InetAddress.getByName(args[0]);
		Integer port = Integer.parseInt(args[1]);
		trackerAddress = new InetSocketAddress(ip, port);
		
		// Get the username and password
		username = args[2];
		password = args[3];
		
		// Get the downloads directory (if present)
		if (args.length > 4)
			downloadsDirectory = new URI(args[4]);
		else
			downloadsDirectory = null;
	}
	catch (Exception e)
	{
		// If anything goes wrong, toss an IAE; main() will print the usage info and exit
		throw new IllegalArgumentException("Error parsing command-line arguments");
	}
}

public static void closeSocketsAndExit(int exitCode)
{
	// Shut down the listener
	if ((peerListener != null) && peerListener.isAlive())
	{
		try
		{
			System.out.print("Shutting down peer listener... ");
			peerListener.setListening(false);
			peerListener.join();
			System.out.println("done");
		}
		catch (Exception E)
		{
			System.out.println();
			E.printStackTrace();
		}
	}
	
	// Close the tracker connections
	if (writeStream != null)
	{
		try
		{
			System.out.print("Closing output stream to tracker... ");
			writeStream.close();
			System.out.println("done");
		}
		catch (Exception E)
		{
			System.out.println();
			E.printStackTrace();
		}
	}
	if (readStream != null)
	{
		try
		{
			System.out.print("Closing input stream from tracker... ");
			readStream.close();
			System.out.println("done");
		}
		catch (Exception E)
		{
			System.out.println();
			E.printStackTrace();
		}
	}
	if ((trackerSocket != null) && !trackerSocket.isClosed())
	{
		try
		{
			System.out.print("Shutting down connction to tracker... ");
			trackerSocket.close();
			System.out.println("done");
		}
		catch (Exception E)
		{
			System.out.println();
			E.printStackTrace();
		}
	}
	
	System.out.println("Connections closed, exiting");
	
	System.exit(exitCode);
}

public static void usage()
{
	System.out.println("BitTide Interactive Peer Client");
	System.out.println("usage: java peer.Peer <tracker address> <tracker port> <username> <password> [downloads directory]");
}

}