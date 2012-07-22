package mnatzakanian.zaven.hw4;

import static android.R.layout.simple_spinner_item;
import static java.lang.Integer.parseInt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import mnatzakanian.zaven.hw4.objects.EventMessage;
import mnatzakanian.zaven.hw4.qr.IntentIntegrator;
import mnatzakanian.zaven.hw4.qr.IntentResult;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.javadude.rube.base.Direction;
import com.javadude.rube.base.Event;
import com.javadude.rube.base.R;
import com.javadude.rube.base.Result;
import com.javadude.rube.base.RubeLogic;

public class MainActivity extends Activity implements EventHandler {
	public static final int STOP_CODE = -1;
	private static final String PINWHEEL = "pinwheel";
	private RubeLogic rubeLogic;
	private int posX, posY;
	private RubeClient client;
	private ArrayAdapter<String> eventList;

	// Handles send events: Send the selected event with the direction up at the
	// current position
	private final OnClickListener sendHandler = new OnClickListener() {
		public void onClick(View arg0) {
			Spinner eventSelected = (Spinner) findViewById(R.id.eventSpinner);
			String eventName = eventSelected.getSelectedItem().toString();
			processMessage(new EventMessage(eventName, Direction.UP.toString(), posX, posY));
		}
	};

	// Gadget Selected: A new gadget has been selected, let rubeLogic know
	private final OnItemSelectedListener gadgetSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			rubeLogic.setCurrentGadget(((TextView) view).getText().toString());
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private final PropertyChangeListener gadgetChangeListener = new PropertyChangeListener() {
		public void propertyChange(final PropertyChangeEvent event) {
			gadgetStateChanged();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		rubeLogic = new RubeLogic(getApplicationContext());

		// Initialize widgets
		Button sendButton = (Button) findViewById(R.id.sendButton);

		final Spinner gadgetSelector = (Spinner) findViewById(R.id.gadgetSpinner);
		final Spinner eventSelector = (Spinner) findViewById(R.id.eventSpinner);

		// Simple_spinner_item code from android developement guide
		SpinnerAdapter gadgetAdapter = new ArrayAdapter<String>(this, simple_spinner_item, rubeLogic.getGadgetNames());
		gadgetSelector.setAdapter(gadgetAdapter);
		eventList = new ArrayAdapter<String>(getApplicationContext(), simple_spinner_item);
		eventSelector.setAdapter(eventList);

		rubeLogic.addPropertyChangeListener(gadgetChangeListener);
		sendButton.setOnClickListener(sendHandler);
		gadgetSelector.setOnItemSelectedListener(gadgetSelectedListener);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (client != null) {
			register();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (client != null) {
			client.close();
		}
	}

	private boolean openConnection() {
		if (client != null) {
			client.open(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
			integrator.initiateScan();
			break;
		}
		return false; // Allow others to consume the selection event
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			String contents = scanResult.getContents();
			String[] coordinates = contents.split(",");
			setCoordinates(coordinates[0], coordinates[1]);
		}
	}

	private void setCoordinates(String xString, String yString) {
		TextView xLabel = (TextView) findViewById(R.id.x);
		TextView yLabel = (TextView) findViewById(R.id.y);
		xLabel.setText(xString);
		yLabel.setText(yString);
		posX = parseInt(xString);
		posY = parseInt(yString);
	}

	private Result processMessage(EventMessage eventMessage) {
		Result result = null;
		switch (eventMessage.getEvent()) {
		case Reset:
			reset();
			break;
		case Register:
			register();
			break;
		case Steam:
			if (PINWHEEL.equals(rubeLogic.getCurrentGadget())) {
				Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
				findViewById(R.id.gadgetView).startAnimation(animation);
				animation.setAnimationListener(new AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}

					public void onAnimationRepeat(Animation animation) {
					}

					// Pinwheel spun, the rube machine is done
					public void onAnimationEnd(Animation animation) {
						stop();
					}
				});
			}
			// Continue onto default case to send the event message
		default:
			result = rubeLogic.event(eventMessage.getEvent(), eventMessage.getDirection());
			if (result != null && result.getDirectionsToFire() != null)
				for (Direction direction : result.getDirectionsToFire()) {
					send(new EventMessage(result.getEventToFire(), direction, posX, posY));
				}
			break;
		}
		return result;
	}

	private void reset() {
		rubeLogic.reset();
		gadgetStateChanged();
	}

	private void register() {
		if (client == null) {
			client = new RubeClient(getServerAddress());
		}
		openConnection();
		send(new EventMessage(Event.Register, Direction.NULL, posX, posY));
	}

	private void send(EventMessage event) {
		try {
			client.send(event);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stop() {
		send(new EventMessage(Event.Release, Direction.NULL, STOP_CODE, 0));
	}

	private String getServerAddress() {
		return ((TextView) findViewById(R.id.serverText)).getText().toString();
	}

	private int gadgetStateChanged() {
		updateEventList();

		ImageView gadgetView = (ImageView) findViewById(R.id.gadgetView);
		String drawableName = rubeLogic.getDrawableNameForCurrentState();
		int drawableId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
		Drawable drawableObject = getResources().getDrawable(drawableId);
		gadgetView.setImageDrawable(drawableObject);
		return drawableId;
	}

	private void updateEventList() {
		List<Event> eventsForState = rubeLogic.getEventsForCurrentGadget();
		eventList.clear();
		for (Event event : eventsForState)
			eventList.add(event.toString());
	}

	public void onEvent(final EventMessage eventMessage) {
		runOnUiThread(new Runnable() {
			public void run() {
				processMessage(eventMessage);
			}
		});
	}
}
