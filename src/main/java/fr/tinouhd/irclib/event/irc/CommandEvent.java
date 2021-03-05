package fr.tinouhd.irclib.event.irc;

import fr.tinouhd.irclib.event.IRCEvent;

public class CommandEvent extends IRCEvent
{
	private final String user;
	private final String commandName;
	private final String[] args;

	public CommandEvent(String rawLine, String user, String commandName, String[] args)
	{
		super(rawLine);
		this.user = user;
		this.commandName = commandName;
		this.args = args;
	}

	public String getUser()
	{
		return user;
	}

	public String getCommandName()
	{
		return commandName;
	}

	public String[] getArguments()
	{
		return args;
	}
}
