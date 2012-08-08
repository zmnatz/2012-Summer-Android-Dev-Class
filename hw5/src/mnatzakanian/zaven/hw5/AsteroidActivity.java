package mnatzakanian.zaven.hw5;

import static mnatzakanian.zaven.hw5.Asteroid.OFFSET;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;

public class AsteroidActivity extends CameraActivity {
	private static final int ASTEROID_MOVE_RATE = 50;
	private static final int ASTEROID_CREATION_RATE = 4000;
	private static final int MAX_ASTEROIDS = 4;
	private static final int STARTING_LIVES = 5;

	private float[] shot;
	private final Set<Asteroid> asteroids = new HashSet<Asteroid>();
	public final Collection<Asteroid> asteroidsToClean = new HashSet<Asteroid>();
	private int score;
	private int lives;

	protected Timer asteroidCreateTimer;
	private final TimerTask asteroidGenerator = new TimerTask() {
		@Override
		public void run() {
			if (asteroids.size() < MAX_ASTEROIDS) {
				asteroids.add(new Asteroid());
			}
		}
	};
	protected Timer asteroidMoveTimer;
	private final TimerTask asteroidMover = new TimerTask() {
		@Override
		public void run() {
			for (Asteroid asteroid : asteroids) {
				boolean hit = asteroid.getCloser();
				if (hit) {
					destroyAsteroid(hit, asteroid);
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lives = STARTING_LIVES;

		setGameHUD(new AsteroidGameDrawer(this));
		addContentView(getGameHUD(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		asteroidCreateTimer = new Timer();
		asteroidMoveTimer = new Timer();
		asteroidCreateTimer.scheduleAtFixedRate(asteroidGenerator, 1000, ASTEROID_CREATION_RATE);
		asteroidMoveTimer.scheduleAtFixedRate(asteroidMover, 4000, ASTEROID_MOVE_RATE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (lives < 1)
			restartGame();
		else
			fireWeapons(event.getX(), event.getY());
		return super.onTouchEvent(event);
	}

	/**
	 * Restart the game by reseting lives and score
	 */
	private void restartGame() {
		asteroidsToClean.addAll(asteroids);
		lives = STARTING_LIVES;
		score = 0;
	}

	/**
	 * Destroy the given asteroid. If the destruction is a result of hitting the
	 * user, subtract a life. Otherwise, add one to the score
	 * 
	 * @param hit True if the destruction is a result of getting hit. False if
	 *            its a result of shooting it
	 * @param asteroid Asteroid to destroy
	 */
	protected void destroyAsteroid(boolean hit, Asteroid asteroid) {
		if (!asteroidsToClean.contains(asteroid) && isAlive()) {
			asteroidsToClean.add(asteroid);
			if (hit) {
				lives--;
			} else {
				score++;
			}
			getVibrator().vibrate(400);
		}
	}

	/**
	 * Fire weapons and destroy asteroids if possible
	 * 
	 * @param x X coordinate on the screen
	 * @param y Y coordinate on the screen
	 */
	private void fireWeapons(float x, float y) {
		if (shot == null) {
			shot = new float[] { x, y };
			for (Asteroid asteroid : asteroids) {
				if (asteroid.isHit(x, y, getGameHUD().getXpixelsPerDegree()))
					destroyAsteroid(false, asteroid);
			}
		}
	}

	/**
	 * @return True if the user has lives left, false otherwise
	 */
	private boolean isAlive() {
		return lives > 0;
	}

	class AsteroidGameDrawer extends DrawOnTop {
		private static final int LINE_THICKNESS = 4;
		private static final int HUD_LIFE_RADIUS = 25;
		private static final int HUD_OFFSET = 40;
		private Paint yellow;
		private Paint thickGreen;
		private Paint red;
		private boolean shotSideToggle;
		private int directlyBehindInPixels;
		private Paint bigGreen;

		public AsteroidGameDrawer(Context context) {
			super(context);
		}

		private void initColors() {
			yellow = new Paint();
			yellow.setStrokeWidth(5);
			yellow.setColor(Color.YELLOW);
			yellow.setStyle(Style.FILL);
			thickGreen = new Paint(getGreen());
			thickGreen.setStrokeWidth(LINE_THICKNESS);
			thickGreen.setStyle(Style.FILL);
			red = new Paint();
			red.setColor(Color.RED);
			red.setStyle(Style.FILL);
			bigGreen = new Paint(thickGreen);
			bigGreen.setTextSize(getResources().getDimension(R.dimen.big_text_size));
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (yellow == null) {
				initColors();
			}
			directlyBehindInPixels = getWidth() / 2 + 180 * getXpixelsPerDegree();
			drawHUD(canvas);
			drawAsteroids(canvas);
			drawShot(canvas);
		}

		/**
		 * Draw the HUD, score, lives, etc...
		 * 
		 * @param canvas Canvas to draw them on
		 */
		private void drawHUD(Canvas canvas) {
			if (isAlive()) {
				canvas.drawText(Integer.toString(score), getWidth() - HUD_OFFSET, HUD_OFFSET, getGreen());
				// Draw Lives
				for (int i = 0; i < STARTING_LIVES; i++) {
					canvas.drawCircle(HUD_OFFSET + i * HUD_LIFE_RADIUS * 2 + 5, HUD_OFFSET, HUD_LIFE_RADIUS,
							i < lives ? thickGreen : getGreen());
				}
			} else {
				canvas.drawText(getContext().getString(R.string.lossMessage), HUD_OFFSET, HUD_OFFSET * 2, bigGreen);
				canvas.drawText(getContext().getString(R.string.restartMessage), getWidth() / 5, getHeight()
						- HUD_OFFSET, bigGreen);
			}
		}

		/**
		 * Draw asteroids on the screen if visible, indicators otherwise
		 * 
		 * @param canvas Canvas to draw them on
		 */
		private synchronized void drawAsteroids(Canvas canvas) {
			asteroids.removeAll(asteroidsToClean);
			asteroidsToClean.clear();

			Set<Float> leftIndicators = new HashSet<Float>();
			Set<Float> rightIndicators = new HashSet<Float>();

			for (Asteroid asteroid : asteroids) {
				float x = convertToPixels(asteroid.getPosition());
				float y = asteroid.getDistance() * getHeight();

				asteroid.setX(x);
				asteroid.setY(y);
				int size = asteroid.getSize(getXpixelsPerDegree());

				if (isToRight(x - size))
					rightIndicators.add(y);
				else if (isToLeft(x + size))
					leftIndicators.add(y);
				else
					canvas.drawCircle(x, y, size, red);
			}
			drawIndicators(canvas, leftIndicators, rightIndicators);
		}

		/**
		 * Draw indicators for off screen asteroids
		 * 
		 * @param canvas Canvas to draw them on
		 * @param left y-coordinates of indicators on the left
		 * @param right y-coordinates of indicators on the right
		 */
		private synchronized void drawIndicators(Canvas canvas, Collection<Float> left, Collection<Float> right) {
			for (float y : left) {
				drawIndicator(canvas, 0, y);
			}
			for (float y : right) {
				drawIndicator(canvas, getWidth(), y);
			}
		}

		private void drawIndicator(Canvas canvas, float x, float y) {
			float xOffset = x > 0 ? x - OFFSET : OFFSET;
			canvas.drawLines(new float[] { x, y, xOffset, y + OFFSET, x, y, xOffset, y - OFFSET }, thickGreen);
		}

		/**
		 * Draw the current shot to the GUI. Clear the shot so another one can
		 * be made
		 * 
		 * @param canvas Canvas to draw it on
		 */
		private void drawShot(Canvas canvas) {
			if (shot != null) {
				canvas.drawLine(shotSideToggle ? 0 : getWidth(), getHeight(), shot[0], shot[1], yellow);
				shotSideToggle = !shotSideToggle;
				shot = null;
			}
		}

		/** Utility Methods for Drawing asteroids **/

		/**
		 * Determines if the coordinate is closer to the left of the screen
		 * 
		 * @param x X coordinate, in pixels, converted from degrees
		 * @return Returns true if the coordinate is offscreen to the left
		 */
		private boolean isToLeft(float x) {
			return directlyBehindInPixels < x || x < 0;
		}

		/**
		 * Determines if the coordinate is closer to the left of the screen
		 * 
		 * @param x X coordinate, in pixels, converted from degrees
		 * @return Returns true if the coordinate is offscreen to the right
		 */
		private boolean isToRight(float x) {
			return getWidth() < x && x < directlyBehindInPixels;
		}

		/**
		 * Convert the given position to coordinates.
		 * 
		 * @param position Poisition (out of 360 degrees)
		 * @return the given position as it relates to pixels on the screen.
		 */
		private float convertToPixels(float position) {
			int left = getLeftBoundary();
			int right = getRightBoundary();
			return (position - left) / (right - left) * getWidth();
		}
	}
}
