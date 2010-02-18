package peer;

import java.net.*;
import java.util.*;
import java.io.*;
import message.*;

public class Peer
{
	private static final int MAX_PORT_VALUE = 0x0000FFFF;	// 65535

	private static InetSocketAddress trackerAddress = null;
	private static PeerToTrackerConnection trackerConnection = null;
	
	private static String username = null;
	private static String password = null;
	
	private static URI downloadsDirectory = null;
	
	private static int listenPort = 0;
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
		System.err.println("error parsing command-line arguments: " + badArgs.getMessage());
		usage();
		System.exit(1);
	}
	catch (IndexOutOfBoundsException OOB)
	{
		usage();
		System.exit(1);
	}
	
	// Create a thread to listen for incoming peer connections
	try
	{
		System.out.print("Starting peer listener... ");
		peerListener = new PeerListenerThread(listenPort);
		peerListener.start();
		System.out.println("done");
	}
	catch (IOException IOE)
	{
		System.out.println();
		System.err.println("error starting peer listener: " + IOE.getMessage());
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
	catch (IOException IOE)
	{
		System.out.println();
		System.err.println("error connecting to tracker: " + IOE.getMessage());
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
	logoutAndExit(0);
}

public static void parseArguments(String[] args)
	throws IllegalArgumentException, IndexOutOfBoundsException
{
	try
	{
		// Attempt to parse and look up the tracker address
		InetAddress address = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);
		trackerAddress = new InetSocketAddress(address, port);
	}
	catch (UnknownHostException UHE)
	{
		throw new IllegalArgumentException("could not find host " + args[0]);
	}
	catch (NumberFormatException NFE)
	{
		throw new IllegalArgumentException("<tracker port> must be an integer between 0 and " + MAX_PORT_VALUE + ", inclusive");
	}
	catch (IllegalArgumentException IAE)
	{
		throw new IllegalArgumentException("<tracker port> must be an integer between 0 and " + MAX_PORT_VALUE + ", inclusive");
	}
	
	try
	{
		// Get the peer listening port
		listenPort = Integer.parseInt(args[2]);
		
		// Check for a valid port number
		if ((listenPort < 0) || (listenPort > MAX_PORT_VALUE))
			throw new NumberFormatException();
	}
	catch (NumberFormatException NFE)
	{
		throw new IllegalArgumentException("<peer listen port> must be an integer between 0 and " + MAX_PORT_VALUE + ", inclusive");
	}
	
	// Get the username and password
	username = args[3];
	password = args[4];
	
	try
	{
		// Get the downloads directory
		downloadsDirectory = new URI(args[5]);
	}
	catch (URISyntaxException URI)
	{
		throw new IllegalArgumentException("<downloads directory> must be a valid path");
	}
}

public static void logoutAndExit(int exitCode)
{
	// FIXME: WRITEME
	
	closeConnectionsAndExit(exitCode);
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
	System.out.println("usage: java peer.Peer <tracker address> <tracker port> <peer listen port> <username> <password> <downloads directory>");
}

}