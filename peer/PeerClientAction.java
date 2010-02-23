package peer;

public enum PeerClientAction
{
	invalidAction	("invalid"),
	findFile		("find"),
	printDownloads	("print"),
	stopDownloads	("stop"),
	exitProgram		("exit");
	
	private String commandName;
	
private PeerClientAction(String command)
{
	commandName = command;
}

public String getCommandName()
{
	return commandName;
}

public String toString()
{
	return commandName;
}

public static PeerClientAction getActionByCommand(String command)
{
	char firstChar = command.charAt(0);
	
	switch (firstChar)
	{
		case 'f':
			return findFile;
		case 'p':
			return printDownloads;
		case 's':
			return stopDownloads;
		case 'e':
		case 'q':
			return exitProgram;
		default:
			break;
	}
	
	return invalidAction;
}

public static void printCommands()
{
	// Print the list of commands (excepting the "invalid" command)
	for (PeerClientAction action : PeerClientAction.values())
	{
		if (action != invalidAction)
			System.out.println("\t" + action);
	}
}

}