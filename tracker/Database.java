package tracker;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintStream;

public class Database
{
	private Map<String, UserRecord> userDB;
	private Map<String, Set<String>> fileDB;
	private String UserDBPath;
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
	public String usersString()
	{
		StringBuilder sb = new StringBuilder();
		for (UserRecord user : userDB.values())
		{
			sb.append(user.toString());
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	public void writeSelf()
	{
		PrintStream out = new PrintStream(userDBPath);
		out.print(userString());
		out.close();
	}
	public Database(String userDBPath)
		throws FileNotFoundException
	{
		this.userDBPath = userDBPath;
		// TODO: possibly split constructor into seperate static functions
		Scanner fileDBScanner = new Scanner(new File(fileDBPath));
		fileDBScanner.useDelimiter("\n");

		String userID;
		String filename;
		Set<String> userIDs;
		UserRecord userRecord;
		Scanner singleFileScanner;

		Scanner userDBScanner = new Scanner(new File(userDBPath));
		userDBScanner.useDelimiter("\n");

		userDB = new Hashtable<String, UserRecord>();
		fileDB = new Hashtable<String, Set<String>>();

		while (userDBScanner.hasNext())
		{
			userRecord = new UserRecord(userDBScanner.next());
			userDB.put(userRecord.getUserID(), userRecord);
		}

		userDBScanner.close();

		for (Map.Entry<String, UserRecord> entry : userDB.entrySet())
		{
			for (String filename : entry.getValue().getFilenames())
			{
				fileDB.put(filename, entry.getKey());
			}
		}
	}
}
