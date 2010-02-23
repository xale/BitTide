package tracker;

import message.*;
import java.util.SortedSet;
import java.util.TreeSet;
import java.net.InetSocketAddress;

class Tracker
{
	private Database db;
	public Message login(String username, String password, InetSocketAddress addr)
	{
		UserRecord user;
		user = db.getUserRecordFromID(username);
		if (user == null) // new user
		{
			user = new UserRecord(username, password, addr);
			db.addUser(user);
		}
		else if (user.getPassword() != password) // wrong password
		{
			return new ErrorMessage("That's not your password!");
		}
		
		if (user.getLogState() != LogState.logout)
		{
			return new ErrorMessage("Not logged out");
		}

		user.login();

		return new SuccessMessage();
		// return an error message if it fails
	}
	public Message logoutReq(String username)
	{
		UserRecord user;
		user = db.getUserRecordFromID(username);
		
		if (user == null)
		{
			return new ErrorMessage("Unknown user");
		}

		if (user.getLogState() != LogState.login)
		{
			return new ErrorMessage("Not logged in");
		}

		user.loginactive();

		return new SuccessMessage();
	}
	public Message fileInfo(String username, String filename, long file_size, FileBitmap fileBitmap)
	{
		UserRecord user;
		user = db.getUserRecordFromID(username);

		assert (user != null); // this case should have been taken care of by previous methods.

		user.addFilename(filename);
		user.setFileBitmap(filename, fileBitmap);
		user.setFileSize(filename, file_size);
		return new SuccessMessage();
	}
	public Message fileBitmap(String username, String filename, FileBitmap fileBitmap)
	{
		UserRecord user;
		user = db.getUserRecordFromID(username);

		assert (user != null); // this case should have been taken care of by previous methods.
		assert (user.hasFilename(filename)); // same comment as above

		user.setFileBitmap(filename, fileBitmap);
		return new SuccessMessage();
	}
	public Message searchReq(String filename)
	{
		//TODO: logic
		SearchReplyPeerEntry[] peers;
		long sizeOfFile;
		return new SearchReplyMessage(sizeOfFile, peers);
	}
	private UserRecord[] bestUsers(String filename)
	{
		Set<UserRecord> userSet = db.getUsersFromFilename(filename);
		if (userSet.size() <= 5)
		{
			return userSet.toArray();
		}
		UserRecord[] users = userSet.toArray();
		return sort(users, new UserRecordComparator(filename));
	}
}
