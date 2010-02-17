package peer;

import java.net.*;
import java.util.*;
import java.io.*;
import message.*;

public class Peer
{
	private static InetSocketAddress trackerAddress = null;
	private static PeerToTrackerConnection trackerConnection = null;
	
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
		peerListener.start();
		System.out.println("done");
	}
	catch (IOException E)
	{
		System.out.println();
		System.err.println("error starting peer listener");
		closeConnectionsAndExit(1);
	}
	
	// Attempt to connect to the server
	try
	{
		System.out.print("Connecting to tracker " + trackerAddress + "... ");
		trackerConnection = new PeerToTrackerConnection(trackerAddress);
		System.out.println("done");
	}
	catch (UnknownHostException UHE)
	{
		System.out.println();
		System.err.println("error connecting to tracker: could not find host " + trackerAddress.getAddress());
		closeConnectionsAndExit(1);
	}
	catch (IOException E)
	{
		System.out.println();
		System.err.println("error connecting to tracker: could not get streams to host " + trackerAddress.getAddress());
		closeConnectionsAndExit(1);
	}
	
	// Create a login message
	Message message = null;
	try
	{
		// Create the message with the listener port, and the user's username and password
		System.out.print("Logging in to tracker... ");
		int port = peerListener.getListenSocket().getLocalPort();
		message = new LoginMessage(port, username, password);
	}
	catch (IllegalArgumentException IAE)
	{
		System.out.println();
		System.err.println("error logging in: " + IAE.getMessage());
		closeConnectionsAndExit(1);
	}
	
	// Send the message to the server
	try
	{
		// Send the message
		trackerConnection.sendMessage(message);
		
		// Wait for a response from the server
		message = trackerConnection.nextMessage();
		
		System.out.println("done");
	}
	catch (ErrorMessageException EME)
	{
		System.out.println();
		System.err.println("error logging in: " + EME.getMessage());
		closeConnectionsAndExit(1);
	}
	catch (EOFException EOFE)
	{
		System.out.println();
		System.err.println("error logging in: the tracker closed the connection");
		closeConnectionsAndExit(1);
	}
	catch (IOException IOE)
	{
		System.out.println();
		System.err.println("error logging in: a network error occurred: " + IOE.getMessage());
		closeConnectionsAndExit(1);
	}
	
	// Send the list of files we're serving to the server
	// FIXME: WRITEME
	
	// Enter interactive loop
	// FIXME: WRITEME
	
	// Close everything and exit
	closeConnectionsAndExit(0);
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
		throw new IllegalArgumentException("error parsing command-line arguments");
	}
}

public static void logoutAndExit(int exitCode)
{
	// FIXME: WRITEME
	
	closeStreamsAndExit(exitCode);
}

public static void closeConnectionsAndExit(int exitCode)
{
	// Close the tracker connection
	if ((trackerConnection != null) && !trackerConnection.isClosed())
	{
		try
		{
			System.out.print("Shutting down connection to tracker... ");
			trackerConnection.close();
			System.out.println("done");
		}
		catch (Exception E)
		{
			System.out.println();
			E.printStackTrace();
		}
	}
	
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
	
	System.out.println("Connections closed, exiting");
	
	System.exit(exitCode);
}

public static void usage()
{
	System.out.println("BitTide Interactive Peer Client");
	System.out.println("usage: java peer.Peer <tracker address> <tracker port> <username> <password> [downloads directory]");
}

}