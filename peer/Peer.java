package peer;

import java.net.*;
import java.util.*;

public class Peer
{
	private static InetSocketAddress trackerAddress;
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
		return;
	}
	
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
		{
			// FIXME: WRITEME
		}
	}
	catch (Exception e)
	{
		throw new IllegalArgumentException();
	}
}

public static void usage()
{
	System.out.println("BitTide Peer Client");
	System.out.println("usage: java Peer <tracker address> <tracker port> <username> <password> [downloads directory]");
}

}