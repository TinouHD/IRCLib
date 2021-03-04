package fr.tinouhd.irclib.event.irc;

import fr.tinouhd.irclib.event.IRCEvent;

public class LineReadEvent extends IRCEvent
{
	public LineReadEvent(String rawLine)
	{
		super(rawLine);
	}
}
