package tracker;

import message.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

class Network
{
	private Tracker tracker;
	private ServerSocket serverSocket;

	public Network(int port, String userDB)
	{
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
	}
	private class Client implements Runnable
	{
		private Socket socket;
		private MessageInputStream readStream;
		private MessageOutputStream writeStream;

		public Client(Socket socket) throws IOException
		{
			this.socket = socket;
			readStream = new MessageInputStream(socket.getInputStream());
			writeStream = new MessageOutputStream(socket.getOutputStream());
		}
		public void run()
		{
		}
	}
}
