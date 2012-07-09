package mnatzakanian.zaven.hw3;

import static mnatzakanian.zaven.hw3.beans.Contact.FORMATTER;
import static mnatzakanian.zaven.hw3.utils.ContactHelpers.CONTACT_PARAM;
import static mnatzakanian.zaven.hw3.utils.ContactHelpers.NEW_CONTACT_ID;
import static mnatzakanian.zaven.hw3.utils.ContactHelpers.loadContact;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import mnatzakanian.zaven.hw3.beans.Contact;
import mnatzakanian.zaven.hw3.provider.ContactListProvider;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class EditContactActivity extends Activity {
	private static final int BIRTHDAY_PICKER_ID = 0;
	private Contact workingContact;

	private EditText mobileNumber;
	private EditText firstName;
	private EditText lastName;
	private EditText displayName;
	private EditText homeNumber;
	private EditText workNumber;
	private EditText emailAddr;
	private TextView birthday;

	// Code Source: Android Developer Guide (developer.android.com)
	private final OnDateSetListener birthdayListener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			birthday.setText(FORMATTER.format(new Date(year, monthOfYear, dayOfMonth)));
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);

		initializeFormElements();

		// Need to check saveInstanceState in case the display is rotated
		if (savedInstanceState != null) {
			refreshView((Contact) savedInstanceState.getParcelable(CONTACT_PARAM));
		} else {
			refreshView();
		}
	}

	/**
	 * Retrieve all the form objects from the layout xml
	 */
	private void initializeFormElements() {
		firstName = (EditText) findViewById(R.id.firstName);
		lastName = (EditText) findViewById(R.id.lastName);
		displayName = (EditText) findViewById(R.id.displayName);
		birthday = (TextView) findViewById(R.id.birthday);
		birthday.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				showDialog(BIRTHDAY_PICKER_ID);
			}
		});
		mobileNumber = (EditText) findViewById(R.id.mobileNumber);
		homeNumber = (EditText) findViewById(R.id.homeNumber);
		workNumber = (EditText) findViewById(R.id.workNumber);
		emailAddr = (EditText) findViewById(R.id.emailAddr);

		// Setup Buttons
		Button save = (Button) findViewById(R.id.submitButton);
		Button cancel = (Button) findViewById(R.id.cancelButton);

		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveContact();
				finish();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	private void refreshView() {
		Contact contact = loadContact(this);
		if (contact == null)
			contact = new Contact();
		refreshView(contact);
	}

	private void refreshView(Contact contact) {
		workingContact = contact;
		firstName.setText(contact.getFirstName());
		lastName.setText(contact.getLastName());
		displayName.setText(contact.getDisplayName());
		if (contact.getBirthday() == null)
			birthday.setText(R.string.set_birthday);
		else
			birthday.setText(contact.getBirthdayString());
		mobileNumber.setText(contact.getMobilePhone());
		homeNumber.setText(contact.getHomePhone());
		workNumber.setText(contact.getWorkPhone());
		emailAddr.setText(contact.getEmailAddress());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(CONTACT_PARAM, workingContact);
	}

	/**
	 * Save form data to the contact object
	 * 
	 * @return Contact object
	 */
	private Contact saveContact() {
		workingContact.setFirstName(firstName.getText().toString());
		workingContact.setLastName(lastName.getText().toString());
		workingContact.setDisplayName(displayName.getText().toString());
		if (!birthday.getText().toString().equals(R.string.set_birthday)) {
			try {
				workingContact.setBirthday(birthday.getText().toString());
			} catch (ParseException e) {
			}
		}
		workingContact.setMobilePhone(mobileNumber.getText().toString());
		workingContact.setHomePhone(homeNumber.getText().toString());
		workingContact.setWorkPhone(workNumber.getText().toString());
		workingContact.setEmailAddress(emailAddr.getText().toString());

		// Commit Working Contact to the Database
		ContentValues contentValues = workingContact.getContentValues();
		long id = workingContact.getId();
		if (id == NEW_CONTACT_ID) {
			getContentResolver().insert(ContactListProvider.CONTENT_URI, contentValues);
		} else {
			Uri uri = Uri.withAppendedPath(ContactListProvider.CONTENT_URI, id + "");
			getContentResolver().update(uri, contentValues, null, null);
		}
		// End of commit
		return workingContact;
	}

	// Code Source: Android Developer Guide (developer.android.com)
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case BIRTHDAY_PICKER_ID:
			Calendar c = Calendar.getInstance();
			return new DatePickerDialog(this, birthdayListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}
}
