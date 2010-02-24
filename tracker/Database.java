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
	private String FileDBPath;
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
	public String fileString()
	{
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Set<String>> entry : fileDB.entrySet())
		{
			sb.append(entry.getKey());
			sb.append(' ');
			for (String username : entry.getValue())
			{
				sb.append(username);
				sb.append(' ');
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length() - 1);
	}
	public void writeSelf()
	{
		PrintStream out = new PrintStream(userDBPath);
		out.print(userString());
		out.close();
		out = new PrintStream(fileDBPath);
		out.print(fileString());
		out.close();
	}
	public Database(String userDBPath, String fileDBPath)
		throws FileNotFoundException
	{
		this.userDBPath = userDBPath;
		this.fileDBPath = fileDBPath;
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

		while (fileDBScanner.hasNext())
		{
			singleFileScanner = new Scanner(fileDBScanner.next());
			filename = singleFileScanner.next();
			userIDs = new HashSet<String>();
			while (singleFileScanner.hasNext())
			{
				userID = singleFileScanner.next();
				userRecord = userDB.get(userID);

				// the user does not exist
				if (userRecord == null)
				{
					System.err.printf("User ID %d does not exist.", userID);
				}
				else
				{
					// the user has this file
					userRecord.addFilename(filename);
					// this file has this user
					userIDs.add(userID);
				}
			}
			fileDB.put(filename, userIDs);
		}
	}
}
