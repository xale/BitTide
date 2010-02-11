package tracker;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import tracker.UserRecord;
public class Database
{
	private Map<Integer, UserRecord> userDB;
	private Map<String, Set<Integer>> fileDB;
	/**
	  * @return null if the fileName is not in the database,
	  * a set of user ids otherwise.
	  * @param fileName the name of the file to look up.
	  */
	public Set<Integer> getUserIDsFromFileName(String fileName)
	{
		return fileDB.get(fileName);
	}
	/**
	  * @return null if the fileName is not in the database,
	  * a set of user records otherwise.
	  * @param fileName the name of the file to look up.
	  */
	public Set<UserRecord> getUsersFromFileName(String fileName)
	{
		Set<Integer> userIDs = getUserIDsFromFileName(fileName);
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
}
