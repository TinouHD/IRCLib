package fr.tinouhd.ircbot.event.irc;

import fr.tinouhd.ircbot.Channel;
import fr.tinouhd.ircbot.event.IRCEvent;

public class PrivmsgEvent extends IRCEvent
{
	private Channel c;
	private String msg;

	public PrivmsgEvent(String rawLine, Channel c, String msg)
	{
		super(rawLine);
		this.c = c;
		this.msg = msg;
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
