package tracker;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import tracker.UserRecord;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class Database
{
	private Map<Integer, UserRecord> userDB;
	private Map<String, Set<Integer>> fileDB;
	/**
	  * @return null if the filename is not in the database,
	  * a set of user ids otherwise.
	  * @param filename the name of the file to look up.
	  */
	public Set<Integer> getUserIDsFromFileName(String filename)
	{
		return fileDB.get(filename);
	}
	/**
	  * @return null if the filename is not in the database,
	  * a set of user records otherwise.
	  * @param filename the name of the file to look up.
	  */
	public Set<UserRecord> getUsersFromFileName(String filename)
	{
		Set<Integer> userIDs = getUserIDsFromFileName(filename);
		if (userIDs == null)
		{
			return null;
		}
		Set<UserRecord> userRecords = new HashSet<UserRecord>();
		for (Integer uid : userIDs)
		{
			userRecords.add(userDB.get(uid));
		}
		return userRecords;
	}
	/**
	  * @return null if the user id doesn't exist, the record otherwise.
	  * @param uid the integer id of the user.
	  */
	public UserRecord getUserRecordFromID(int uid)
	{
		return userDB.get(uid);
	}
	public Database(String userDBPath, String fileDBPath)
		throws FileNotFoundException
	{
		// TODO: possibly split constructor into seperate static functions
		Scanner fileDBScanner = new Scanner(new File(fileDBPath));
		fileDBScanner.useDelimiter("\n");

		int userID;
		String filename;
		Set<Integer> userIDs;
		UserRecord userRecord;
		Scanner singleFileScanner;

		Scanner userDBScanner = new Scanner(new File(userDBPath));
		userDBScanner.useDelimiter("\n");

		userDB = new Hashtable<Integer,UserRecord>();
		fileDB = new Hashtable<String, Set<Integer>>();

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
			userIDs = new HashSet<Integer>();
			while (singleFileScanner.hasNextInt())
			{
				userID = singleFileScanner.nextInt();
				userRecord = userDB.get(userID);

				// the user does not exist
				if (userRecord == null)
				{
					System.err.printf("User ID %d does not exist.", userID);
				}
				else
				{
					// the user has this file
					userRecord.addFileName(filename);
					// this file has this user
					userIDs.add(userID);
				}
			}
			fileDB.put(filename, userIDs);
		}
	}
}
