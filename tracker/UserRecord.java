package tracker;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.Scanner;
import java.lang.IllegalArgumentException;
import java.util.HashSet;
import tracker.LogState;

public class UserRecord
{
	private final String userID;
	private final String password;
	private final InetSocketAddress address;
	private Set<String> fileNames;
	private LogState logState;

	/**
	  * @return a finalized String.
	  */
	public String getUserID()
	{
		return userID;
	}
	/**
	  * @return a finalized password.
	  */
	public String getPassword()
	{
		return password;
	}
	/**
	  * @return a finalized address.
	  */
	public InetSocketAddress getAddress()
	{
		return address;
	}
	/**
	  * @return the login state of the user.
	  */
	public LogState getLogState()
	{
		return logState;
	}
	/**
	  * @param filename The filename to check against the internal set.
	  * @return true if the internal set contains the filename, false otherwise.
	  */
	public boolean hasFileName(String filename)
	{
		return fileNames.contains(filename);
	}
	/**
	  * @param filename The filename to add to the internal set.
	  */
	public void addFileName(String filename)
	{
		fileNames.add(filename);
	}
	/**
	  * @param filename The filename to remove from the internal set.
	  */
	public void removeFileName(String filename)
	{
		fileNames.remove(filename);
	}
	/**
	  * @param dbString A single line of the file database.
	  */
	public UserRecord(String dbString)
	{
		Scanner scanner = new Scanner(dbString);
		if (! scanner.hasNext())
		{
			throw new IllegalArgumentException("String does not have a username field.");
		}
		userID = scanner.next();
		if (! scanner.hasNext())
		{
			throw new IllegalArgumentException("String does not have a password field.");
		}
		password = scanner.next();
		if (! scanner.hasNext())
		{
			throw new IllegalArgumentException("String does not have an ip address field.");
		}
		String ip = scanner.next();
		if (! scanner.hasNextInt())
		{
			throw new IllegalArgumentException("String does not have an port field.");
		}
		int port = scanner.nextInt();
		address = new InetSocketAddress(ip, port);
		fileNames = new HashSet<String>();
		while (scanner.hasNext())
		{
			fileNames.add(scanner.next());
		}
		
		// Set the default login state of a user to be logged out
		// TODO: is this correct behavior?
		logState = LogState.logout;
	}
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append(userID); s.append(' ');
		s.append(password); s.append(' ');

		// getAddress() returns an InetAddress from an InetSocketAddress
		// getHostAddress() returns the string ip from an InetAddress
		s.append(address.getAddress().getHostAddress()); s.append(' ');
		s.append(address.getPort()); s.append(' ');
		for (String filename : fileNames)
		{
			s.append(filename); s.append(' ');
		}
		return s.toString();
	}
}
