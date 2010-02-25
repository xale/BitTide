package tracker;

import message.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.EOFException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class Network
{
	static void debug(String msg)
	{
		System.err.println(msg);
	}
	public static void main(String[] args) throws IOException
	{
		Tracker tracker;
		ServerSocket serverSocket = null;

		int port = Integer.parseInt(args[0]);
		String userDB = args[1];

		tracker = new Tracker(userDB);
		debug("Opened tracker with \"" + userDB + "\" as database.");

		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			System.err.println("Couldn't open socket:");
			System.err.println(e.getMessage());
			System.exit(1);
		}
		debug("Opened socket listening on port " + port + ".");

		ExecutorService threadPool = Executors.newCachedThreadPool();
		boolean flag = true;
		Socket socket;

		while (flag)
		{
			socket = serverSocket.accept();
			debug("Accepting connection.");
			threadPool.execute(new Client(socket, tracker));
		}
	}
	private static class Client implements Runnable
	{
		private Socket socket;
		private Database db;
		private Tracker tracker;
		private MessageInputStream readStream;
		private MessageOutputStream writeStream;
		private String username;

		public Client(Socket socket, Tracker tracker) throws IOException
		{
			this.socket = socket;
			this.tracker = tracker;
			this.db = tracker.db;
			readStream = new MessageInputStream(socket.getInputStream());
			writeStream = new MessageOutputStream(socket.getOutputStream());
		}
		public void run()
		{
			try
			{
				Message message;
				message = readStream.readMessage();
				debug("Got message.");
				if (message.getMessageCode() != MessageCode.LoginMessageCode)
				{
					writeStream.writeMessage(new ErrorMessage("You are not logged in."));
					socket.close();
					return;
				}
				username = ((LoginMessage) message).getUsername();
				String password = ((LoginMessage) message).getPassword();

				debug("Attempting to connect with " + username + ":" + password + ".");
				int port = ((LoginMessage) message).getListenPort();
				message = tracker.login(username, password, new InetSocketAddress(socket.getInetAddress(), port));
				writeStream.writeMessage(message);
				if (message.getMessageCode() != MessageCode.SuccessMessageCode)
				{
					debug("Error logging in; exiting thread.");
					socket.close();
					return;
				}
				boolean flag = true;
				while (flag)
				{
					if (db.getUserRecordFromID(username).getLogState() == LogState.login)
					{
						message = readStream.readMessage();
						debug("Got a message.");
						switch (message.getMessageCode())
						{
							case SearchRequestMessageCode:
								message = handleSearchRequest((SearchRequestMessage) message);
								break;
							case FileInfoMessageCode:
								message = handleFileInfo((FileInfoMessage) message);
								break;
							case FileBitmapMessageCode:
								message = handleFileBitmap((FileBitmapMessage) message);
								break;
							case LogoutRequestMessageCode:
								message = handleLogoutRequest((LogoutRequestMessage) message);
								break;
							default:
								message = new ErrorMessage("Bad message type.");
						}
						writeStream.writeMessage(message);
					}
					else if (db.getUserRecordFromID(username).getLogState() == LogState.inactive)
					{
						message = readStream.readMessage();
						debug("Got a message.");
						switch (message.getMessageCode())
						{
							case FileBitmapMessageCode:
								message = handleFileBitmap((FileBitmapMessage) message);
								break;
							case LogoutCompleteMessageCode:
								message = handleLogoutComplete((LogoutCompleteMessage) message);
								break;
						}
					}
				}
			}
			catch (EOFException e)
			{
				debug("Got EOFException.");
				debug("Client has disconnected.");
				tracker.logoutReq(username);
				tracker.logoutComplete(username);
			}
			catch (IOException e)
			{
				debug("Got IOException.");
				try
				{
					socket.close();
				}
				catch (IOException wtfJava)
				{
				}
			}
		}
		private Message handleSearchRequest(SearchRequestMessage message)
		{
			debug("Received search request.");
			String filename = message.getFilename();
			debug("Searching for " + filename + ".");
			return tracker.searchReq(username, filename);
		}
		private Message handleFileInfo(FileInfoMessage message)
		{
			debug("Received file info.");
			String filename = message.getFilename();
			debug("Updating " + filename + ".");
			long file_size = message.getFileSize();
			FileBitmap bitmap = message.getFileBitmap();
			return tracker.fileInfo(username, filename, file_size, bitmap);
		}
		private Message handleFileBitmap(FileBitmapMessage message)
		{
			debug("Received file bitmap.");
			String filename = message.getFilename();
			debug("Updating " + filename + ".");
			FileBitmap bitmap = message.getFileBitmap();
			return tracker.fileBitmap(username, filename, bitmap);
		}
		private Message handleLogoutRequest(LogoutRequestMessage message)
		{
			debug("Received logout request.");
			return tracker.logoutReq(username);
		}
		private Message handleLogoutComplete(LogoutCompleteMessage message)
		{
			debug("Received logout complete.");
			return tracker.logoutComplete(username);
		}
	}
}
