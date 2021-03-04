package fr.tinouhd.ircbot.event;

public abstract class IRCEvent extends Event
{
	private final String rawLine;

	public IRCEvent(String rawLine)
	{
		this.rawLine = rawLine;
	}

	public String getRawLine()
	{
		return rawLine;
	}
}
