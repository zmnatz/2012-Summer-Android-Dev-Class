package mnatzakanian.zaven.hw3;

import static mnatzakanian.zaven.hw3.utils.ContactHelpers.editContact;
import static mnatzakanian.zaven.hw3.utils.ContactHelpers.extractIdFromActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DisplayContactActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_fragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editItem:
			editContact(this, extractIdFromActivity(this));
			return true; // Consume the menu item selected event
		}
		return false; // Allow others to consume the selection event
	}
}
