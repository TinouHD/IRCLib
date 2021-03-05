package fr.tinouhd.irclib;

import fr.tinouhd.irclib.event.EventManager;
import fr.tinouhd.irclib.event.irc.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

	/**
	 * Create a new {@code IRCConnection} with default port.
	 * @param host the hostname/IP address of the IRC server.
	 * @param name the username you want to use.
	 * @throws IOException if something is wrong with the socket connection.
	 * @throws IRCException if the IRC connection send an error code.
	 */
	public IRCConnection(String host, String name) throws IOException, IRCException
	{
		this(host, 6667, name);
	}

	/**
	 * Create a new {@code IRCConnection}.
	 * @param host the hostname/IP address of the IRC server.
	 * @param port the port to use for the connection.
	 * @param name the username you want to use.
	 * @throws IOException if something is wrong with the socket connection.
	 * @throws IRCException if the IRC connection send an error code.
	 */
	public IRCConnection(String host, int port, String name) throws IOException, IRCException
	{
		this(host, port, name, null);
	}

	/**
	 * Create a new {@code IRCConnection} with default port.
	 * @param host the hostname/IP address of the IRC server.
	 * @param name the username you want to use.
	 * @param pass the password to auth you.
	 * @throws IOException if something is wrong with the socket connection.
	 * @throws IRCException if the IRC connection send an error code.
	 */
	public IRCConnection(String host, String name, String pass) throws IOException, IRCException
	{
		this(host, 6667, name, pass);
	}

	/**
	 * Create a new {@code IRCConnection}.
	 * @param host the hostname/IP address of the IRC server.
	 * @param port the port to use for the connection.
	 * @param name the username you want to use.
	 * @param pass the password to auth you.
	 * @throws IOException if something is wrong with the socket connection.
	 * @throws IRCException if the IRC connection send an error code.
	 * @see #connect()
	 * @see #register()
	 * @see #start()
	 */
	public IRCConnection(String host, int port, String name, String pass) throws IOException, IRCException
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
			} else if(line.matches("$* ([4-5][0-9]{2}) *^"))
			{
				close();
				throw new IRCException(Integer.parseInt(Pattern.compile("$* ([4-5][0-9]{2}) *^").matcher(line).group(1)));
			}
		}

		start();
	}

	/**
	 * Create the connection to the {@code Socket} and prepare the {@code BufferedWriter} and {@code BufferedReader}.
	 * @throws IOException if an I/O error occurs when creating the {@code Socket}.
	 */
	private void connect() throws IOException
	{
		socket = new Socket(host, port);
		socket.setSoTimeout(0);
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	/**
	 * Quit all channels and close.
	 * @see #close()
	 */
	private void disconnect() throws IOException
	{
		channels.keySet().forEach(n -> {
			try
			{
				quitChannel(n);
			} catch (IOException e)
			{}
		});

		close();
	}

	/**
	 * Register the user on the IRC connection.
	 * @throws IOException if an I/O error occurs.
	 */
	private void register() throws IOException
	{
		if(pass != null) writer.write("PASS " + pass + "\r\n");
		writer.write("NICK " + name + "\r\n");
		writer.write("USER " + name + " localhost " + host + " " + name + "\r\n");
		writer.flush();
	}

	/**
	 * Create and return the channel object and join the specified channel.
	 * @param name the name of the channel to join. (without the #)
	 * @return the {@code Channel}.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	public Channel joinChannel(String name) throws IOException
	{
		if(name.startsWith("#")) name = name.substring(1);
		Channel c = new Channel(name, writer);
		channels.put(name, c);
		return c;
	}

	/**
	 * Quit the specified channel.
	 * @param name the name of the channel to quit. (without the #)
	 * @throws IOException if an I/O error occurs.
	 */
	public void quitChannel(String name) throws IOException
	{
		if(name.startsWith("#")) name = name.substring(1);
		Channel c = channels.remove(name);
		c.quit();
	}

	/**
	 * Send an raw command on the IRC connection.
	 * @param command the raw command.
	 * @throws IOException if an I/O error occurs.
	 */
	public void sendRaw(String command) throws IOException
	{
		command = command.endsWith("\r\n") ? command : command + "\r\n";
		writer.write(command);
		writer.flush();
	}

	/**
	 * Start the thread that read lines from the IRC connection.
	 */
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

	/**
	 * Process raw line from the IRC connection.
	 * @param line the line to process.
	 * @throws IOException if an I/O error occurs.
	 */
	private void processLine(String line) throws IOException
	{
		String[] args = line.split(" ");
		em.callEvent(new LineReadEvent(line));
		if(line.startsWith("@"))
		{
			processLine(line.substring(line.indexOf(" ") + 1));
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
					String msg = String.join(" ", Arrays.copyOfRange(args, 3, args.length)).substring(1);
					if(msg.startsWith(commandProvider))
					{
						em.callEvent(new CommandEvent(line, args[0].split("!")[0].substring(1), msg.split(" ")[0].substring(1), Arrays.copyOfRange(msg.split(" "), 1, msg.split(" ").length)));
					}else
					{
						em.callEvent(new PrivmsgEvent(line, args[0].split("!")[0].substring(1), channels.get(args[2].substring(1)), msg));
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

	/**
	 * @return the {@code EventManager} for this {@code IRCConnection}.
	 */
	public EventManager getEventManager()
	{
		return em;
	}

	/**
	 * Command provider is the prefix of the command name. (default "!")
	 * @return the "command provider".
	 */
	public String getCommandProvider()
	{
		return commandProvider;
	}

	/**
	 * Command provider is the prefix of the command name. (default "!")
	 * @param commandProvider the new command provider.
	 */
	public void setCommandProvider(String commandProvider)
	{
		this.commandProvider = commandProvider;
	}

	/**
	 * Stop the {@code Thread}, disconnect from all {@code Channel}, close the {@code BufferedWriter}, close the {@code BufferedReader} and close close the socket connection.
	 * @throws IOException if an I/O error occurs.
	 */
	@Override public void close() throws IOException
	{
		t.interrupt();
		disconnect();
		writer.close();
		reader.close();
		socket.close();
	}
}
