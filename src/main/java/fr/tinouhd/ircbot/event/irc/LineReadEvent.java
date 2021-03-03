package fr.tinouhd.ircbot.event.irc;

import fr.tinouhd.ircbot.event.IRCEvent;

public class LineReadEvent extends IRCEvent
{
	public LineReadEvent(String rawLine)
	{
		super(rawLine);
	}
}
