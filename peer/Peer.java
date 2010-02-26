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
	
	private static PeerDownloadManager downloadManager = null;
	private static File downloadsDirectory = null;
	
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
		usage();
		System.err.println("error: " + badArgs.getMessage());
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
		peerListener = new PeerListenerThread(listenPort, downloadsDirectory);
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
		System.out.println("done");
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
		System.out.print("Logging in to tracker... ");
		
		// Create a login message with the listener port, and the user's username and password
		int port = peerListener.getListenSocket().getLocalPort();
		Message message = new LoginMessage(port, username, password);
		
		// Attempt to send the message (will throw an ErrorMessageException if the reply from the tracker is an error)
		trackerConnection.sendMessage(message);
		
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
	
	// Create a download manager
	System.out.print("Creating download manager... ");
	downloadManager = new PeerDownloadManager(trackerConnection, downloadsDirectory);
	System.out.println("done");
	
	// Send the list of files we're seeding to the tracker
	System.out.print("Sending seeded file list to tracker... ");
	File[] sharedFiles = downloadsDirectory.listFiles();
	for (File sharedFile : sharedFiles)
	{
		try
		{
			trackerConnection.sendMessage(new FileInfoMessage(sharedFile));
		}
		catch (ErrorMessageException e)
		{
			System.out.println();
			System.err.println("error sending file info for file " + sharedFile.getName() + ".");
		}
		catch (EOFException EOFE)
		{
			System.out.println();
			System.err.print("error sending file info for file " + sharedFile.getName() + ":");
			System.err.println(" tracker closed connection.");
			closeConnectionsAndExit(1);
		}
		catch (IOException IOE)
		{
			System.out.println();
			System.err.print("error sending file info for file " + sharedFile.getName() + ":");
			System.err.println(" a network error occurred: " + IOE.getMessage());
			closeConnectionsAndExit(1);
		}
	}
	System.out.println("done");
	
	// Wrap all user interaction in a try block for network errors
	try
	{
		// Set up keyboard input scanner
		Scanner keyboard = new Scanner(System.in);
		
		// Enter interactive loop
		String[] inputLine;
		PeerClientAction action;
		Message reply;
		do
		{
			// Print the command prompt
			System.out.print("peer> ");
		
			// Read the user's next command
			try
			{
				inputLine = keyboard.nextLine().split(" ");
				action = PeerClientAction.getActionByCommand(inputLine[0]);
			}
			catch (IndexOutOfBoundsException noCommand)
			{
				action = PeerClientAction.invalidAction;
				continue;
			}
			
			// Determine what to do
			switch (action)
			{
				case findFile:
				{
					// Send a search request with the specified file name
					String filename = null;
					try
					{
						filename = inputLine[1];
					}
					catch (IndexOutOfBoundsException noFilename)
					{
						System.out.println("usage: find <filename>");
						continue;
					}
					
					try
					{
						// Send the request
						reply = trackerConnection.sendMessage(new SearchRequestMessage(filename));
					}
					catch (ErrorMessageException EME)
					{
						System.err.println("search error: " + EME.getMessage());
						continue;
					}
					
					// Check for the right message type in the reply
					if (reply.getMessageCode() != MessageCode.SearchReplyMessageCode)
					{
						System.err.println("warning: reply to SearchRequest is not a SearchReply");
						continue;
					}
					
					// Check the results returned
					SearchReplyMessage searchReply = (SearchReplyMessage)reply;
					SearchReplyPeerEntry[] results = searchReply.getPeerResults();
					if (results == null)
					{
						System.out.println("no peer results");
						continue;
					}
					
					// Print the number of peers with the file
					System.out.println(results.length + " peers found");
					
					// Ask if the user would like to download the file
					System.out.print("would you like to begin downloading? (y/N) ");
					
					// Read the user's response
					String response = keyboard.nextLine();
					
					// If the user doesn't want to download, go back to main prompt
					try
					{
						if (response.charAt(0) != 'y')
							continue;
					}
					catch (IndexOutOfBoundsException noResponse)
					{
						continue;
					}
					
					// Otherwise, attempt download the file
					try
					{
						System.out.print("beginning download... ");
						downloadManager.startDownload(filename, searchReply);
						System.out.println("download started");
					}
					catch (RuntimeException RE)
					{
						System.out.println();
						System.err.println("couldn't start download: " + RE.getMessage());
					}
					
					break;
				}
				case printDownloads:
				{
					// Print the list of downloads
					downloadManager.printDownloadStatusList();
					
					break;
				}
				case stopDownloads:
				{
					// Check if there are any downloads in progress
					int downloads = downloadManager.getNumDownloadsInProgress();
					if (downloads < 1)
					{
						System.out.println("no downloads in progress");
						continue;
					}
					
					if (downloads > 1)
					{
						System.out.print("are you sure you want to cancel " + downloads + " downloads in progress? (y/N) ");
					}
					else
					{
						System.out.print("are you sure you want to cancel the download in progress? (y/N) ");
					}
					
					// Get the user's response
					String response = keyboard.nextLine();
					
					// If the user doesn't want to stop the downloads, do nothing
					try
					{
						if (response.charAt(0) != 'y')
							continue;
					}
					catch (IndexOutOfBoundsException noResponse)
					{
						continue;
					}
					
					// Otherwise, cancel all downloads, and send the most up-to-date bitmaps to the tracker
					System.out.print("stopping downloads... ");
					downloadManager.stopDownloads(true);
					System.out.println("downloads canceled");
					
					break;
				}
				case exitProgram:
				{
					// Check if there are downloads in progress
					int downloads = downloadManager.getNumDownloadsInProgress();
					if (downloads > 0)
					{
						// Warn the user about incomplete downloads
						if (downloads > 1)
						{
							System.out.println("there are " + downloads + " downloads in progress");
							System.out.println("exiting now will cancel these downloads");
						}
						else
						{
							System.out.println("there is one download in progress");
							System.out.println("exiting now will cancel this download");
						}
						
						// Confirm if the user wants to quit
						System.out.print("are you sure you wish to exit? (y/N) ");
						
						// Read the user's response
						String response = keyboard.nextLine();
						
						// If the user doesn't wish to quit, return to the prompt
						try
						{
							if (response.charAt(0) != 'y')
							{
								action = PeerClientAction.invalidAction;
								continue;
							}
						}
						catch (IndexOutOfBoundsException noResponse)
						{
							action = PeerClientAction.invalidAction;
							continue;
						}
					}
					
					// Otherwise, fall through and let the interaction loop terminate
					
					break;
				}
				default:
				{
					// Print a warning
					System.out.println("unknown command: " + inputLine[0]);
					System.out.println("valid commands:");
					PeerClientAction.printCommands();
					break;
				}
			}
			
		} while (action != PeerClientAction.exitProgram);
	}
	catch (EOFException EOFE)
	{
		System.out.println();
		System.err.println("error: the tracker closed the connection");
		closeConnectionsAndExit(1);
	}
	catch (IOException IOE)
	{
		System.out.println();
		System.err.println("a network error occurred: " + IOE.getMessage());
		closeConnectionsAndExit(1);
	}
	
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
	
	// Get the path to the downloads directory
	downloadsDirectory = new File(args[5]);
	
	// Check if the path exists
	if (!downloadsDirectory.exists())
	{
		// Attempt to create the downloads directory
		if (!downloadsDirectory.mkdir())
		{
			throw new IllegalArgumentException("could not create downloads directory " + downloadsDirectory);
		}
	}
	else if (!downloadsDirectory.isDirectory())
	{
		throw new IllegalArgumentException("<downloads directory> must be a valid path to a directory");
	}
}

public static void logoutAndExit(int exitCode)
{
	// Send a logout message
	try
	{
		System.out.print("Logging out from tracker... ");
		trackerConnection.sendMessage(new LogoutRequestMessage(username));
		System.out.println("done");
	}
	catch (Exception E)
	{
		System.out.println();
		System.err.println("an exception occurred while logging out: " + E.getMessage());
	}
	
	closeConnectionsAndExit(exitCode);
}

public static void closeConnectionsAndExit(int exitCode)
{
	// Stop the download manager
	if (downloadManager != null)
	{
		System.out.print("Shutting down download manager... ");
		downloadManager.shutdown();
		System.out.println("done");
	}
	
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
