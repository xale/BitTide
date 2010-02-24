package tracker;

import message.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class Network
{
	public static void main(String[] args) throws IOException
	{
		final Tracker tracker;
		ServerSocket serverSocket = null;

		int port = Integer.parseInt(args[0]);
		String userDB = args[1];
		tracker = new Tracker(userDB);

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

		ExecutorService threadPool = Executors.newCachedThreadPool();
		boolean flag = true;
		Socket socket;

		while (flag)
		{
			socket = serverSocket.accept();
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
				if (message.getMessageCode() != MessageCode.LoginMessageCode)
				{
					writeStream.writeMessage(new ErrorMessage("You are not logged in."));
					socket.close();
					return;
				}
				String username = ((LoginMessage) message).getUsername();
				String password = ((LoginMessage) message).getPassword();
				int port = ((LoginMessage) message).getListenPort();
				message = tracker.login(username, password, new InetSocketAddress(socket.getInetAddress(), port));
				writeStream.writeMessage(message);
				if (message.getMessageCode() != MessageCode.SuccessMessageCode)
				{
					socket.close();
					return;
				}
			}
			catch (IOException e)
			{
				try
				{
					socket.close();
				}
				catch (IOException wtfJava)
				{
				}
			}
		}
	}
}
