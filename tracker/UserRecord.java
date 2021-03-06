package tracker;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Scanner;
import java.lang.IllegalArgumentException;
import message.FileBitmap;

public class UserRecord
{
	private final String userID;
	private final String password;
	private final InetSocketAddress address;
	private Set<String> filenames;
	private Map<String, FileBitmap> bitmaps;
	private Map<String, Long> sizemaps;
	private LogState logState;

	/**
	  * @return a finalized String.
	  */
	public String getUserID()
	{
		return userID;
	}
	public boolean equals(UserRecord o)
	{
		return o.getUserID().equals(userID);
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
	public void login()
	{
		logState = LogState.login;
	}
	public void logout()
	{
		logState = LogState.logout;
	}
	public void loginactive()
	{
		logState = LogState.inactive;
	}
	/**
	  * @param filename The filename to check against the internal set.
	  * @return true if the internal set contains the filename, false otherwise.
	  */
	public boolean hasFilename(String filename)
	{
		return filenames.contains(filename);
	}
	public final Set<String> getFilenames()
	{
		return filenames;
	}
	/**
	  * @param filename The filename to add to the internal set.
	  */
	public void addFilename(String filename)
	{
		filenames.add(filename);
		bitmaps.put(filename, new FileBitmap());
		sizemaps.put(filename, Long.valueOf(0));
	}
	/**
	  * Does not check that file exists
	  * @param bitmap The bitmap to give it.
	  */
	public void setFileBitmap(String filename, FileBitmap bitmap)
	{
		bitmaps.put(filename, bitmap);
	}
	public final FileBitmap getFileBitmap(String filename)
	{
		return bitmaps.get(filename);
	}
	/**
	  * Does not check that file exists
	  * @param size The size to give it.
	  */
	public void setFileSize(String filename, long size)
	{
		sizemaps.put(filename, size);
	}
	public long getFileSize(String filename)
	{
		return sizemaps.get(filename);
	}
	/**
	  * @param filename The filename to remove from the internal set.
	  */
	public void removeFilename(String filename)
	{
		filenames.remove(filename);
		bitmaps.remove(filename);
		sizemaps.remove(filename);
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
		userID = scanner.next().trim();
		if (! scanner.hasNext())
		{
			throw new IllegalArgumentException("String does not have a password field.");
		}
		password = scanner.next().trim();
		if (! scanner.hasNext())
		{
			throw new IllegalArgumentException("String does not have an ip address field.");
		}
		String ip = scanner.next().trim();
		if (! scanner.hasNextInt())
		{
			throw new IllegalArgumentException("String does not have an port field.");
		}
		int port = scanner.nextInt();
		address = new InetSocketAddress(ip, port);
		filenames = new ConcurrentSkipListSet<String>();
		bitmaps = new ConcurrentHashMap<String, FileBitmap>();
		sizemaps = new ConcurrentHashMap<String, Long>();
		String filename;
		while (scanner.hasNext())
		{
			filename = scanner.next();
			filenames.add(scanner.next());
			bitmaps.put(filename, new FileBitmap());
			sizemaps.put(filename, Long.valueOf(0));
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
		for (String filename : filenames)
		{
			s.append(filename); s.append(' ');
		}
		return s.toString();
	}

	public UserRecord(String uid, String pass, InetSocketAddress addr)
	{
		userID = uid;
		password = pass;
		address = addr;
		filenames = new ConcurrentSkipListSet<String>();
		bitmaps = new ConcurrentHashMap<String, FileBitmap>();
		sizemaps = new ConcurrentHashMap<String, Long>();
		logState = LogState.logout;
	}
}
