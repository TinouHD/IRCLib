package fr.tinouhd.ircbot;

import fr.tinouhd.ircbot.event.*;
import fr.tinouhd.ircbot.event.irc.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IRCConnection implements AutoCloseable
{
	private final String host;
	private final int port;
	private final String name;
	private final String pass;

	private String commandProvider = "!";

	private Socket socket;

	private BufferedWriter writer;
	private BufferedReader reader;

	private final Map<String, Channel> channels = new HashMap<>();

	private final EventManager em = new EventManager();

	private Thread t;

	public IRCConnection(String host, String name) throws IOException
	{
		this(host, 6667, name);
	}

	public IRCConnection(String host, int port, String name) throws IOException
	{
		this(host, port, name, null);
	}

	public IRCConnection(String host, String name, String pass) throws IOException
	{
		this(host, 6667, name, pass);
	}

	public IRCConnection(String host, int port, String name, String pass) throws IOException
	{
		this.host = host;
		this.port = port;
		this.name = name;
		this.pass = pass;
		connect();
		register();

		String line;
		while ((line = reader.readLine()) != null)
		{
			System.out.println(line);
			if (line.contains("004"))
			{
				System.out.println(">>> Connection successful !");
				writer.write("PONG " + line.split(" ")[0] + "\r\n");
				writer.flush();
				break;
			} else if (line.contains("433"))
			{
				System.exit(1);
			}
		}

		start();
	}

	private void connect() throws IOException
	{
		socket = new Socket(host, port);
		socket.setSoTimeout(0);
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	private void disconnect() throws IOException
	{
		channels.forEach((n,c) -> {
			try
			{
				c.quit();
			} catch (IOException e)
			{}
		});
	}

	private void register() throws IOException
	{
		if(pass != null) writer.write("PASS " + pass + "\r\n");
		writer.write("NICK " + name + "\r\n");
		writer.write("USER " + name + " localhost " + host + " " + name + "\r\n");
		writer.flush();
	}

	public Channel joinChannel(String name) throws IOException
	{
		Channel c = new Channel(name, writer);
		channels.put(name, c);
		return c;
	}

	public void quitChannel(String name) throws IOException
	{
		Channel c = channels.get(name);
		c.quit();
	}

	public void sendRaw(String command) throws IOException
	{
		command = command.endsWith("\r\n") ? command : command + "\r\n";
		writer.write(command);
		writer.flush();
	}

	private void start()
	{
		t = new Thread(() -> {
			try
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					System.out.println(line);
					processLine(line);
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}, "IRC_Listener");

		t.start();
	}

	private void processLine(String line) throws IOException
	{
		String[] args = line.split(" ");
		em.callEvent(new LineReadEvent(line));
		if(line.startsWith("@"))
		{
			processLine(line.substring(line.indexOf(" ")));
		}else if(line.startsWith(":"))
		{
			switch (args[1])
			{
				case "JOIN":
					em.callEvent(new JoinEvent(line, channels.get(args[2].substring(1)), args[0].split("!")[0].substring(1)));
					break;
				case "PART":
					em.callEvent(new PartEvent(line, channels.get(args[2].substring(1)), args[0].split("!")[0].substring(1)));
					break;
				case "PRIVMSG":
					String msg = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
					if(msg.startsWith(commandProvider))
					{
						em.callEvent(new CommandEvent(line, msg.split(" ")[0].substring(1), Arrays.copyOfRange(msg.split(" "), 1, msg.split(" ").length)));
					}else
					{
						em.callEvent(new PrivmsgEvent(line, channels.get(args[2].substring(1)), msg));
					}
					break;
				default:
					break;
			}
		}else
		{
			switch (args[0])
			{
				case "PING":
					System.out.println(line.replace("PING", "PONG"));
					writer.write(line.replace("PING", "PONG"));
					writer.flush();
					em.callEvent(new PingEvent(line));
					break;
				default:
					break;
			}
		}
	}

	public EventManager getEventManager()
	{
		return em;
	}

	public String getCommandProvider()
	{
		return commandProvider;
	}

	public void setCommandProvider(String commandProvider)
	{
		this.commandProvider = commandProvider;
	}

	@Override public void close() throws Exception
	{
		t.interrupt();
		disconnect();
		writer.close();
		reader.close();
		socket.close();
	}
}
