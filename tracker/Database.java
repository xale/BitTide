package tracker;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.PrintStream;

public class Database
{
	private Map<String, UserRecord> userDB;
	private Map<String, Set<String>> fileDB;
	private String userDBPath;
	/**
	  * @return null if the filename is not in the database,
	  * a set of user ids otherwise.
	  * @param filename the name of the file to look up.
	  */
	public Set<String> getUserIDsFromFilename(String filename)
	{
		return fileDB.get(filename);
	}
	/**
	  * @return null if the filename is not in the database,
	  * a set of user records otherwise.
	  * @param filename the name of the file to look up.
	  */
	public Set<UserRecord> getUsersFromFilename(String filename)
	{
		Set<String> userIDs = getUserIDsFromFilename(filename);
		if (userIDs == null)
		{
			return null;
		}
		Set<UserRecord> userRecords = new HashSet<UserRecord>();
		for (String uid : userIDs)
		{
			userRecords.add(userDB.get(uid));
		}
		return userRecords;
	}
	public void addUser(UserRecord user)
	{
		userDB.put(user.getUserID(), user);
		fileDB.put(user.getUserID(), new HashSet<String>());
	}
	/**
	  * @return null if the user id doesn't exist, the record otherwise.
	  * @param uid the integer id of the user.
	  */
	public UserRecord getUserRecordFromID(String uid)
	{
		return userDB.get(uid);
	}
	public String userString()
	{
		StringBuilder sb = new StringBuilder();
		for (UserRecord user : userDB.values())
		{
			sb.append(user.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	public void writeSelf()
	{
		Network.debug("Writing to disk.");
		File tmpfile = null;
		File outfile = new File(userDBPath);
		Network.debug("writeSelf: outfile=" + outfile.toString() + ".")
		try
		{
			tmpfile = File.createTempFile("wtf", ".tmp", outfile.getParentFile());
			Network.debug("writeSelf: tmpfile=" + tmpfile.toString() + ".")
		}
		catch (IOException e)
		{
			System.err.println("This should not have happened.");
			System.err.println(e.getMessage());
			System.exit(1);
		}
		PrintStream out = null;
		try
		{
			out = new PrintStream(tmpfile);
		}
		catch (FileNotFoundException e)
		{
			System.err.println("This should not have happened.");
			System.err.println(e.getMessage());
			System.exit(1);
		}
		out.print(userString());
		out.close();

		outfile.delete();
		tmpfile.renameTo(outfile);
		Network.debug("Finished writing to disk.");
	}
	public Database(String userDBPath)
		throws FileNotFoundException
	{
		this.userDBPath = userDBPath;

		String userID;
		Set<String> userIDs;
		UserRecord userRecord;
		Scanner singleFileScanner;

		Scanner userDBScanner = new Scanner(new File(userDBPath));
		userDBScanner.useDelimiter("\n");

		userDB = new Hashtable<String, UserRecord>();
		fileDB = new Hashtable<String, Set<String>>();

		try
		{
			while (userDBScanner.hasNext())
			{
				userRecord = new UserRecord(userDBScanner.next());
				userDB.put(userRecord.getUserID(), userRecord);
			}
			userDBScanner.close();
		}
		catch (NullPointerException e)
		{
		}

		for (Map.Entry<String, UserRecord> entry : userDB.entrySet())
		{
			for (String filename : entry.getValue().getFilenames())
			{
				userIDs = fileDB.get(filename);
				if (userIDs == null)
				{
					userIDs = new HashSet<String>();
					fileDB.put(filename, userIDs);
				}
				userIDs.add(entry.getKey());
			}
		}
	}
}
