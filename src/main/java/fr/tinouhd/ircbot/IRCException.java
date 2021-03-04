package fr.tinouhd.ircbot;

public class IRCException extends Exception
{
	private final int errCode;

	public IRCException(int errCode, String message)
	{
		super(message);
		this.errCode = errCode;
	}

	public IRCException(int errCode)
	{
		this(errCode, "ERROR code : " + errCode);
	}

	public int getErrorCode()
	{
		return errCode;
	}
}
