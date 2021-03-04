package fr.tinouhd.irclib.event.irc;

import fr.tinouhd.irclib.Channel;
import fr.tinouhd.irclib.event.IRCEvent;

public class PartEvent extends IRCEvent
{
	private final Channel c;
	private final String user;

	public PartEvent(String rawLine, Channel c, String user)
	{
		super(rawLine);
		this.c = c;
		this.user = user;
	}

	public Channel getChannel()
	{
		return c;
	}

	public String getUser()
	{
		return user;
	}
}
