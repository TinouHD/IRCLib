package fr.tinouhd.ircbot.event;

import javafx.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventManager
{

	private final Map<String, List<Pair<Listener, Method>>> handlers = new HashMap<>();

	public void addListener(Listener listener)
	{
		for (Method m : listener.getClass().getMethods())
		{
			if(m.isAnnotationPresent(EventHandler.class) && m.getReturnType() == void.class && m.getParameterCount() == 1 && IRCEvent.class.isAssignableFrom(m.getParameters()[0].getType()))
			{
				if(handlers.containsKey(m.getParameters()[0].getType().getCanonicalName()))
				{
					handlers.get(m.getParameters()[0].getType().getCanonicalName()).add(new Pair<>(listener, m));
				}else
				{
					handlers.put(m.getParameters()[0].getType().getCanonicalName(), new ArrayList<>(Collections.singletonList(new Pair<>(listener, m))));
				}
			}
		}
	}

	public void callEvent(Event event)
	{
		if(handlers.containsKey(event.getClass().getCanonicalName()))
		{
			handlers.get(event.getClass().getCanonicalName()).forEach(p -> {
				try
				{
					p.getValue().invoke(p.getKey(), event);
				} catch (IllegalAccessException | InvocationTargetException e)
				{
					e.printStackTrace();
				}
			});
		}
	}
}
