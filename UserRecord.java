import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.Scanner;
import java.lang.IllegalArgumentException;
import java.util.HashSet;

public class UserRecord
{
	private final int userID;
	private final String password;
	private final InetSocketAddress address;
	private Set<String> fileNames;

	/**
	  * @return a finalized int.
	  */
	public int getUserID()
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
	  * @param fileName The filename to check against the internal set.
	  * @return true if the internal set contains the filename, false otherwise.
	  */
	public boolean hasFileName(String fileName)
	{
		return fileNames.contains(fileName);
	}
	/**
	  * @param fileName The filename to add to the internal set.
	  */
	public void addFileName(String fileName)
	{
		fileNames.add(fileName);
	}
	/**
	  * @param fileName The filename to remove from the internal set.
	  */
	public void removeFileName(String fileName)
	{
		fileNames.remove(fileName);
	}
	/**
	  * @param dbString A single line of the file database.
	  */
	public UserRecord(String dbString) throws UnknownHostException
	{
		Scanner scanner = new Scanner(dbString);
		if (! scanner.hasNextInt())
		{
			throw new IllegalArgumentException("String does not start with an int.");
		}
		userID = scanner.nextInt();
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
		try
		{
			address = new InetSocketAddress(InetAddress.getByName(ip), port);
		}
		catch (UnknownHostException e)
		{
			// log exception
			System.err.printf("'%s' could not be found.", ip);
			throw e;
		}
		fileNames = new HashSet();
		while (scanner.hasNext())
		{
			fileNames.add(scanner.next());
		}
	}
}
