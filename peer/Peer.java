package peer;

import java.net.*;
import java.util.*;
import java.io.*;
import message.*;

public class Peer
{
	private static final int MAX_PORT_VALUE = 0x0000FFFF;	// 65535
	
	private static PeerToTrackerConnection trackerConnection = null;
	
	private static String username = null;
	private static String password = null;
	
	private static URI downloadsDirectory = null;
	
	private static int listenPort = 0;
	private static PeerListenerThread peerListener = null;
	
	private static Scanner keyboard = null;

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
	
	// Attempt to connect to the tracker
	try
	{
		System.out.print("Connecting to tracker " + trackerConnection.getAddress() + "... ");
		trackerConnection.open();
		System.out.print("done");
	}
	catch (IOException IOE)
	{
		System.out.println();
		System.err.println("could not connect, a network error occurred: " + IOE.getMessage());
		closeConnectionsAndExit(1);
	}
	
	// Log in to the tracker
	try
	{
		System.out.print("Logging in... ");
		
		// Create a login message with the listener port, and the user's username and password
		int port = peerListener.getListenSocket().getLocalPort();
		Message message = new LoginMessage(port, username, password);
		
		// Attempt to send the message, and get the tracker's reply
		Message loginReply = trackerConnection.sendMessage(message);
		
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
		System.err.println("could not log in, a network error occurred: " + IOE.getMessage());
		closeConnectionsAndExit(1);
	}
	
	// Send the list of files we're serving to the server
	// FIXME: WRITEME
	
	// Set up keyboard input scanner
	keyboard = new Scanner(System.in);
	
	// Enter interactive loop
	String command;
	PeerClientAction action;
	do
	{
		// Print the command prompt
		System.out.println("peer> ");
	
		// Read the user's next command
		command = keyboard.next();
		action = PeerClientAction.getActionByCommand(command);
		
		// Determine what to do
		switch (action)
		{
			case findFile:
				// FIXME: WRITEME: find file
				break;
			
			case printDownloads:
				// FIXME: WRITEME: print current downloads
				break;
			
			case stopDownloads:
				// FIXME: WRITEME: stop current downloads
				break;
			
			case exitProgram:
				// Does nothing; do/while loop will terminate
				break;
			
			default:
				// Print a warning
				System.out.println("unknown command: " + command);
				System.out.println(" valid commands:");
				PeerClientAction.printCommands();
				break;
		}
		
	} while (action != PeerClientAction.exitProgram);
	
	// Close everything and exit
	logoutAndExit(0);
}

public static void parseArguments(String[] args)
	throws IllegalArgumentException, IndexOutOfBoundsException
{
	// Check for the right number of arguments
	if (args.length != 6)
		throw new IndexOutOfBoundsException();
	
	try
	{
		// Attempt to parse and look up the tracker address
		InetAddress address = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);
		trackerConnection = new PeerToTrackerConnection(new InetSocketAddress(address, port));
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
	// Send a logout message
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
			System.out.print("Closing connection to tracker... ");
			trackerConnection.close();
			System.out.println("done");
		}
		catch (Exception E)
		{
			System.out.println();
			E.printStackTrace();
		}
	}
	
	// Shut down the peer listener
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