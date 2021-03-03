package fr.tinouhd.ircbot.event.irc;

import fr.tinouhd.ircbot.Channel;
import fr.tinouhd.ircbot.event.IRCEvent;

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
