package tracker;

import message.*;
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
	public Message fileInfo(long file_size, FileBitmap fileBitmap, String filename)
	{
		//TODO: logic
		return new SuccessMessage();
	}
	public Message searchReq(String filename)
	{
		//TODO: logic
		// SearchReplyPeerEntry[] peers
		return new SearchReplyMessage(sizeOfFile, peers);
	}
	public Message fileBitmap(String filename, FileBitmap fileBitmap)
	{
		//TODO: logic
		return new SuccessMessage();
	}
}
