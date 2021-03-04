package fr.tinouhd.irclib;

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

	/**
	 * Send a message in this {@code Channel}.
	 * @param msg the message.
	 * @throws IOException if an I/O error occurs.
	 */
	public void sendMessage(String msg) throws IOException
	{
		writer.write("PRIVMSG #" + name + " :" + msg + "\r\n");
		writer.flush();
	}

	/**
	 * Quit this {@code Channel}.
	 * @throws IOException if an I/O error occurs.
	 */
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
