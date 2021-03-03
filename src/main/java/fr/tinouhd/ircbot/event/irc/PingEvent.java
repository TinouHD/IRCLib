package fr.tinouhd.ircbot.event.irc;

import fr.tinouhd.ircbot.event.IRCEvent;

public class PingEvent extends IRCEvent
{
	public PingEvent(String rawLine)
	{
		super(rawLine);
	}
}
