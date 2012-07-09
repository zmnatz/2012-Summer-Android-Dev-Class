package mnatzakanian.zaven.hw3;

import static android.widget.Toast.LENGTH_SHORT;
import static mnatzakanian.zaven.hw3.utils.ContactHelpers.CONTACT_PARAM;
import static mnatzakanian.zaven.hw3.utils.ContactHelpers.NEW_CONTACT_ID;
import static mnatzakanian.zaven.hw3.utils.ContactHelpers.editContact;
import static mnatzakanian.zaven.hw3.utils.ContactHelpers.extractIdFromActivity;
import mnatzakanian.zaven.hw3.ContactListFragment.ContactSelectedListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ContactsActivity extends FragmentActivity implements ContactSelectedListener {
	public static final int EDIT_CONTACT_REQUEST_ID = 41;

	private DisplayContactFragment display;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_fragment);
		display = (DisplayContactFragment) getSupportFragmentManager().findFragmentById(R.id.display_fragment);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.createItem:
			Intent intent = new Intent(getApplicationContext(), EditContactActivity.class);
			startActivityForResult(intent, EDIT_CONTACT_REQUEST_ID);
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_list_menu, menu);
		return true;
	}

	public void onContactSelected(long id) {
		if (display == null || !display.isInLayout()) {
			Intent intent = new Intent(getApplicationContext(), DisplayContactActivity.class);
			intent.putExtra(CONTACT_PARAM, id);
			startActivity(intent);
		} else {
			getIntent().putExtra(CONTACT_PARAM, id);
			display.refreshView();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		long workingContactId = extractIdFromActivity(this);
		switch (item.getItemId()) {
		case R.id.editItem:
			// Check if a contact has been selected, if not, do not continue
			if (workingContactId == NEW_CONTACT_ID) {
				Toast.makeText(getApplicationContext(), "No Contact is Selected", LENGTH_SHORT).show();
				break;
			}
			// If selecting create item, do not need to validate that an item is
			// selected
		case R.id.createItem:
			editContact(this, workingContactId);
			return true; // Consume the menu item selected event
		}
		return false; // Allow others to consume the selection event
	}
}