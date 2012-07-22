package mnatzakanian.zaven.hw4;

import mnatzakanian.zaven.hw4.objects.EventMessage;

public interface EventHandler {
	void onEvent(EventMessage eventMessage);
}
