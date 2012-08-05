package mnatzakanian.zaven.hw5;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public abstract class CameraActivity extends Activity {
	private Camera mCamera;
	private CameraPreview mPreview;
	private Sensor accelerometer;
	private Sensor magnetometer;
	private final float[] mag = new float[3];
	private final float[] accel = new float[3];
	private final float[] rotationMatrix = new float[9];
	private final float[] remappedRotationMatrix = new float[9];
	private final float[] orientation = new float[3];
	private float horizontalViewAngle;
	private int halfHorizontalViewAngle;
	private SensorManager sensorManager;
	private float textSize;
	private double compass;
	private Vibrator vibrator;
	private DrawOnTop gameHUD;

	SensorEventListener accelListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(final SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				System.arraycopy(event.values, 0, mag, 0, 3);
			else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				System.arraycopy(event.values, 0, accel, 0, 3);
			updateOrientation();
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	private void updateOrientation() {
		if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, mag)) {
			SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z,
					remappedRotationMatrix);
			SensorManager.getOrientation(remappedRotationMatrix, orientation);

			compass = (Math.toDegrees(orientation[0]) + 270) % 360;

			getGameHUD().postInvalidate();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		// Create an instance of Camera
		mCamera = getCameraInstance();

		Parameters parameters = mCamera.getParameters();
		horizontalViewAngle = parameters.getHorizontalViewAngle();
		halfHorizontalViewAngle = (int) (horizontalViewAngle / 2);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		textSize = getResources().getDimension(R.dimen.text_size);
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			Log.e("CAMERA", "Camera not available", e);
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(accelListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(accelListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera(); // release the camera immediately on pause event
		sensorManager.unregisterListener(accelListener);

	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	private final long[] vibrationPattern = { 0, 50, 50, 50, 50, 200, 100, 50 };

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		getVibrator().vibrate(vibrationPattern, -1);
		return false;
	}

	public DrawOnTop getGameHUD() {
		return gameHUD;
	}

	public void setGameHUD(DrawOnTop gameHUD) {
		this.gameHUD = gameHUD;
	}

	public Vibrator getVibrator() {
		return vibrator;
	}

	class DrawOnTop extends View {
		private Paint green;
		private int xpixelsPerDegree;
		private int left;
		private int right;

		public DrawOnTop(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (green == null) {
				green = new Paint();
				green.setColor(Color.GREEN);
				green.setStyle(Style.STROKE);
				green.setTextSize(textSize);
			}
			left = (int) compass - halfHorizontalViewAngle;
			right = (int) compass + halfHorizontalViewAngle;

			int chunkSize = 5;

			xpixelsPerDegree = getWidth() / (int) horizontalViewAngle;
			int xpixelsPerChunk = getXpixelsPerDegree() * chunkSize;
			int xoffset = left % chunkSize;
			int xstart = left + chunkSize - xoffset;
			int x = getXpixelsPerDegree() * xoffset;

			for (int i = xstart; i < right; i += chunkSize) {
				String text = "" + (i % 360);
				if (i % 20 == 0)
					canvas.drawText(text, x - green.measureText(text) / 2, getHeight() - 40, green);
				canvas.drawLine(x, getHeight(), x, getHeight() - 30, green);
				x += xpixelsPerChunk;
			}
		}

		protected int getLeftBoundary() {
			return left;
		}

		protected int getRightBoundary() {
			return right;
		}

		public int getXpixelsPerDegree() {
			return xpixelsPerDegree;
		}

		public Paint getGreen() {
			return green;
		}
	}
}
