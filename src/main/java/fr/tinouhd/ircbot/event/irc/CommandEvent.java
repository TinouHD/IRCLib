package fr.tinouhd.ircbot.event.irc;

import fr.tinouhd.ircbot.event.IRCEvent;

public class CommandEvent extends IRCEvent
{
	private String commandName;
	private String[] args;

	public CommandEvent(String rawLine, String commandName, String[] args)
	{
		super(rawLine);
		this.commandName = commandName;
		this.args = args;
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
