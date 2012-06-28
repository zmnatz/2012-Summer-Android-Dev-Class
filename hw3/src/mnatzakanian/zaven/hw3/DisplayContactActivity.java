package mnatzakanian.zaven.hw3;

import static mnatzakanian.zaven.hw3.ContactsActivity.CONTACT_PARAM;
import mnatzakanian.zaven.hw3.beans.Contact;
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

		setViewContact((Contact) getIntent().getParcelableExtra(CONTACT_PARAM));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_menu, menu);
		return true;
	}

	private void setViewContact(Contact contact) {
		workingContact = contact;
		outputToView(R.id.firstName, contact.getFirstName());
		outputToView(R.id.lastName, contact.getLastName());
		outputToView(R.id.displayName, contact.getDisplayName());
		outputToView(R.id.birthday, contact.getBirthdayString());
		outputToView(R.id.mobileNumber, contact.getMobilePhone());
		outputToView(R.id.homeNumber, contact.getHomePhone());
		outputToView(R.id.workNumber, contact.getWorkPhone());
		outputToView(R.id.emailAddr, contact.getEmailAddress());
	}

	private void outputToView(int id, String output) {
		((TextView) findViewById(id)).setText(output);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editItem:
			Intent edit = new Intent(getApplicationContext(), EditContactActivity.class);
			edit.putExtra(CONTACT_PARAM, workingContact.getId());
			startActivityForResult(edit, EDIT_CONTACT_REQUEST);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode && EDIT_CONTACT_REQUEST == requestCode) {
			setViewContact((Contact) data.getParcelableExtra(CONTACT_PARAM));

			Intent results = new Intent();
			results.putExtra(CONTACT_PARAM, workingContact.getId());
			setResult(RESULT_OK, results);
		} else
			super.onActivityResult(requestCode, resultCode, data);
	}
}
