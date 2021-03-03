package fr.tinouhd.ircbot.event;

public abstract class IRCEvent extends Event
{
	private String rawLine;

	public IRCEvent(String rawLine)
	{
		this.rawLine = rawLine;
	}

	public String getRawLine()
	{
		return rawLine;
	}
}
