package fr.tinouhd.irclib.event;

public abstract class Event
{
	private String name;

	protected Event()
	{

	}

	public String getEventName() {
		if (this.name == null) {
			this.name = this.getClass().getSimpleName();
		}

		return this.name;
	}
}
