package mnatzakanian.zaven.hw2;

import static mnatzakanian.zaven.hw2.ContactsActivity.CONTACT_PARAM;
import mnatzakanian.zaven.hw2.beans.Contact;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayContactActivity extends Activity {
	private static final int EDIT_CONTACT_REQUEST = 43;
	private Contact workingContact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_display);

		workingContact = getIntent().getParcelableExtra(CONTACT_PARAM);
		displayContact(workingContact);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_menu, menu);
		return true;
	}

	private void displayContact(Contact contact) {
		((TextView) findViewById(R.id.firstName)).setText(contact
				.getFirstName());
		((TextView) findViewById(R.id.lastName)).setText(contact.getLastName());
		((TextView) findViewById(R.id.displayName)).setText(contact
				.getDisplayName());
		((TextView) findViewById(R.id.birthday)).setText(contact
				.getBirthdayString());
		((TextView) findViewById(R.id.mobileNumber)).setText(contact
				.getMobilePhone());
		((TextView) findViewById(R.id.homeNumber)).setText(contact
				.getHomePhone());
		((TextView) findViewById(R.id.workNumber)).setText(contact
				.getWorkPhone());
		((TextView) findViewById(R.id.emailAddr)).setText(contact
				.getEmailAddress());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editItem:
			Intent edit = new Intent(getApplicationContext(),
					EditContactActivity.class);
			edit.putExtra(CONTACT_PARAM, workingContact);
			startActivityForResult(edit, EDIT_CONTACT_REQUEST);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode && EDIT_CONTACT_REQUEST == requestCode) {
			workingContact = data.getParcelableExtra(CONTACT_PARAM);
			displayContact(workingContact);

			Intent results = new Intent();
			results.putExtra(CONTACT_PARAM, workingContact);
			setResult(RESULT_OK, results);
		} else
			super.onActivityResult(requestCode, resultCode, data);
	}
}
