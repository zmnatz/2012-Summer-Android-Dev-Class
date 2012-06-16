package mnatzakanian.zaven.hw2;

import static mnatzakanian.zaven.hw2.ContactsActivity.CONTACT_PARAM;

import java.util.Date;

import mnatzakanian.zaven.hw2.beans.Contact;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EditContactActivity extends Activity {
	private Contact contact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_edit);
		contact = new Contact();
		Button save = (Button) findViewById(R.id.submitButton);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveContact();
				Intent results = new Intent();
				results.putExtra(CONTACT_PARAM, contact);
				setResult(RESULT_OK, results);
				finish();
			}
		});
		Button cancel = (Button) findViewById(R.id.cancelButton);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(CONTACT_PARAM, contact);
	}

	/**
	 * 
	 */
	private void saveContact() {
		contact = new Contact();
		contact.setFirstName("FIRST NAME");
		contact.setLastName("LAST NAME");
		contact.setDisplayName("NAME");
		contact.setMobilePhone("PHONE");
		contact.setBirthday(new Date());
	}

}
