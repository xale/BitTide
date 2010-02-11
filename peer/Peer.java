package peer;

import java.net.*;
import java.util.*;
import java.io.*;
import message.*;

public class Peer
{
	private static InetSocketAddress trackerAddress;
	
	private static Socket trackerSocket;
	private static InputStream read;
	private static OutputStream write;
	
	private static String username;
	private static String password;
	
	private static URI downloadsDirectory;

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
	
	// Open a listening port
	// FIXME: WRITEME
	
	// Attempt to connect to the server
	try
	{
		trackerSocket = new Socket(trackerAddress.getAddress(), trackerAddress.getPort());
		write = trackerSocket.getOutputStream();
		read = trackerSocket.getInputStream();
	}
	catch (UnknownHostException UHE)
	{
		System.err.println("Cannot connect to host: " + trackerAddress.getAddress());
		System.exit(1);
	}
	catch (IOException E)
	{
		System.err.println("Error getting streams to host " + trackerAddress.getAddress());
		System.exit(1);
	}
	
	// Send a login message
	// FIXME: WRITEME
	
	// Enter interactive loop
	// FIXME: WRITEME
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

public static void usage()
{
	System.out.println("BitTide Interactive Peer Client");
	System.out.println("usage: java peer.Peer <tracker address> <tracker port> <username> <password> [downloads directory]");
}

}