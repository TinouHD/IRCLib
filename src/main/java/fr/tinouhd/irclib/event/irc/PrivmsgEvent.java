package fr.tinouhd.irclib.event.irc;

import fr.tinouhd.irclib.Channel;
import fr.tinouhd.irclib.event.IRCEvent;

public class PrivmsgEvent extends IRCEvent
{
	private final String user;
	private final Channel c;
	private final String msg;

	public PrivmsgEvent(String rawLine, String user, Channel c, String msg)
	{
		super(rawLine);
		this.user = user;
		this.c = c;
		this.msg = msg;
	}

	public String getUser()
	{
		return user;
	}

	public Channel getChannel()
	{
		return c;
	}

	public String getMessage()
	{
		return msg;
	}
}
