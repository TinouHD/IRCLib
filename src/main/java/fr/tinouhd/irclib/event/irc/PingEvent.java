package fr.tinouhd.irclib.event.irc;

import fr.tinouhd.irclib.event.IRCEvent;

public class PingEvent extends IRCEvent
{
	public PingEvent(String rawLine)
	{
		super(rawLine);
	}
}
