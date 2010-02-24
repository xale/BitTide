package tracker;

import message.*;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.Arrays;
import java.net.InetSocketAddress;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

class Tracker
{
	public Database db;
	public Tracker(String userDB) throws IOException, FileNotFoundException
	{
		File file = new File(userDB);
		file.createNewFile();
		if (! file.exists() || ! file.canRead() || ! file.canWrite())
		{
			throw new IOException("Permissions error on " + userDB + ".");
		}
		db = new Database(userDB);
	}
	public void writeToDisk()
	{
		db.writeSelf();
	}
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

		writeToDisk();
		return new SuccessMessage();
		// return an error message if it fails
	}
	public Message logoutComplete(String username)
	{
		UserRecord user;
		user = db.getUserRecordFromID(username);
		
		if (user == null)
		{
			return new ErrorMessage("Unknown user");
		}

		if (user.getLogState() != LogState.inactive)
		{
			return new ErrorMessage("Not inactive");
		}

		user.logout();

		writeToDisk();
		return new SuccessMessage();
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

		writeToDisk();
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

		writeToDisk();
		return new SuccessMessage();
	}
	public Message fileBitmap(String username, String filename, FileBitmap fileBitmap)
	{
		UserRecord user;
		user = db.getUserRecordFromID(username);

		assert (user != null); // this case should have been taken care of by previous methods.
		assert (user.hasFilename(filename)); // same comment as above

		user.setFileBitmap(filename, fileBitmap);

		writeToDisk();
		return new SuccessMessage();
	}
	public Message searchReq(String username, String filename)
	{
		SearchReplyPeerEntry[] peers;
		UserRecord[] users = bestUsers(username, filename);
		if (users == null || users.length == 0)
		{
			return new SearchReplyMessage();
		}
		long sizeOfFile = users[0].getFileSize(filename);
		int numPeers = 5;
		if (users.length < numPeers)
		{
			numPeers = users.length;
		}
		peers = new SearchReplyPeerEntry[numPeers];
		for (int i = 0; i < numPeers; ++i)
		{
			peers[i] = new SearchReplyPeerEntry(users[i].getAddress(), users[i].getFileBitmap(filename));
		}

		return new SearchReplyMessage(sizeOfFile, peers);
	}
	private UserRecord[] bestUsers(String username, String filename)
	{
		Set<UserRecord> userSet = db.getUsersFromFilename(filename);
		for (UserRecord user : userSet)
		{
			if (user.getUserID().equals(username))
			{
				userSet.remove(user);
				break;
			}
		}
		if (userSet.size() <= 5)
		{
			return userSet.toArray(new UserRecord[0]);
		}
		UserRecord[] users = userSet.toArray(new UserRecord[0]);
		Arrays.sort(users, new UserRecordComparator(filename));
		return users;
	}
}
