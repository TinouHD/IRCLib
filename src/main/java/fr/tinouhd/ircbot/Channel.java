package fr.tinouhd.ircbot;

import java.io.BufferedWriter;
import java.io.IOException;

public class Channel
{
	private String name;
	private BufferedWriter writer;

	protected Channel(String name, BufferedWriter writer) throws IOException
	{
		if(name.startsWith("#")) name = name.substring(1);
		this.name = name;
		this.writer = writer;
		writer.write("JOIN #" + name + "\r\n");
	}

	public void sendMessage(String msg) throws IOException
	{
		writer.write("PRIVMSG #" + name + " :" + msg + "\r\n");
		writer.flush();
	}

	protected void quit() throws IOException
	{
		writer.write("PART #" + name + "\r\n");
		writer.flush();
	}

	@Override public String toString()
	{
		return name + "'s channel.";
	}
}
