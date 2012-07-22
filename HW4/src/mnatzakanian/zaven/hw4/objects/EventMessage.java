package mnatzakanian.zaven.hw4.objects;

import com.javadude.rube.base.Direction;
import com.javadude.rube.base.Event;

public class EventMessage {
	private int x, y;
	private String eventName;
	private Event event;
	private String directionName;
	private Direction direction;

	public EventMessage(String eventName, String directionName, int x, int y) {
		super();
		setEventName(eventName);
		setDirectionName(directionName);
		this.x = x;
		this.y = y;
	}

	public EventMessage(Event event, Direction direction, int x, int y) {
		super();
		this.event = event;
		this.eventName = event.toString();
		this.direction = direction;
		this.directionName = direction.toString();
		this.x = x;
		this.y = y;
	}

	public final int getX() {
		return x;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final int getY() {
		return y;
	}

	public final void setY(int y) {
		this.y = y;
	}

	public final void setY(String y) {
		this.y = Integer.parseInt(y);
	}

	public final String getEventName() {
		return eventName;
	}

	public final void setEventName(String eventName) {
		this.eventName = eventName;
		this.event = eventName == null ? null : Event.valueOf(eventName);
	}

	public final String getDirectionName() {
		return directionName;
	}

	public final void setDirectionName(String directionName) {
		this.directionName = directionName;
		this.direction = directionName == null ? null : Direction.valueOf(directionName);
	}

	public final Event getEvent() {
		return event;
	}

	public final Direction getDirection() {
		return direction;
	}
}
