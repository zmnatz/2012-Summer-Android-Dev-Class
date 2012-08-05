package mnatzakanian.zaven.hw5;

public class Asteroid {
	private static final int MAX_SPEED = 2;
	private static final int MAX_SIZE = 15;
	private static final int MIN_SIZE = 2;
	private static final int SIZE_DIFF = MAX_SIZE - MIN_SIZE;
	public static final int STARTING_DISTANCE = 100;
	public static final int OFFSET = 15;

	private final float location;
	private float distance;
	private final double speed;
	private float x;
	private float y;

	public Asteroid() {
		location = (float) (Math.random() * 360);
		distance = STARTING_DISTANCE;
		speed = Math.random() * MAX_SPEED;
	}

	public boolean isHit(float shotX, float shotY, int pixelsPerDegree) {
		int size = getSize(pixelsPerDegree);
		double distance = Math.sqrt(Math.pow(shotX - x, 2) + Math.pow(shotY - y, 2));
		return distance < size;
	}

	public float getPosition() {
		return location;
	}

	public float getDistance() {
		return 1 - distance / STARTING_DISTANCE;
	}

	public boolean getCloser() {
		distance -= speed;
		return distance < 0;
	}

	public int getSize() {
		float distancePercentage = getDistance();
		return (int) (SIZE_DIFF * distancePercentage + MIN_SIZE);
	}

	public int getSize(int pixelsPerDegree) {
		return getSize() * pixelsPerDegree;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

}
