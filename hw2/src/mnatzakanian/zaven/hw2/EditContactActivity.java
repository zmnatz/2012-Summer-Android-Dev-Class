package mnatzakanian.zaven.hw2;

import static mnatzakanian.zaven.hw2.ContactsActivity.CONTACT_PARAM;
import static mnatzakanian.zaven.hw2.beans.Contact.FORMATTER;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import mnatzakanian.zaven.hw2.beans.Contact;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class EditContactActivity extends Activity {
	private static final int BIRTHDAY_PICKER_ID = 0;

	private Contact contact;

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
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			birthday.setText(FORMATTER.format(new Date(year, monthOfYear,
					dayOfMonth)));
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_edit);

		initializeFormElements();
		if (savedInstanceState != null)
			contact = savedInstanceState.getParcelable(CONTACT_PARAM);
		else if (getIntent().hasExtra(CONTACT_PARAM))
			contact = getIntent().getParcelableExtra(CONTACT_PARAM);
		else
			contact = new Contact();

		displayContact(contact);

		// Setup Buttons
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
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	private void initializeFormElements() {
		firstName = (EditText) findViewById(R.id.firstName);
		lastName = (EditText) findViewById(R.id.lastName);
		displayName = (EditText) findViewById(R.id.displayName);
		birthday = (TextView) findViewById(R.id.birthday);
		birthday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showDialog(BIRTHDAY_PICKER_ID);
			}
		});
		mobileNumber = (EditText) findViewById(R.id.mobileNumber);
		homeNumber = (EditText) findViewById(R.id.homeNumber);
		workNumber = (EditText) findViewById(R.id.workNumber);
		emailAddr = (EditText) findViewById(R.id.emailAddr);
	}

	private void displayContact(Contact contact) {
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
		outState.putParcelable(CONTACT_PARAM, contact);
	}

	/**
	 * Save form data to the contact object
	 * 
	 * @return Contact object
	 */
	private Contact saveContact() {
		contact.setFirstName(firstName.getText().toString());
		contact.setLastName(lastName.getText().toString());
		contact.setDisplayName(displayName.getText().toString());
		if (!birthday.getText().toString().equals(R.string.set_birthday)) {
			try {
				contact.setBirthday(birthday.getText().toString());
			} catch (ParseException e) {
			}
		}
		contact.setMobilePhone(mobileNumber.getText().toString());
		contact.setHomePhone(homeNumber.getText().toString());
		contact.setWorkPhone(workNumber.getText().toString());
		contact.setEmailAddress(emailAddr.getText().toString());
		return contact;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case BIRTHDAY_PICKER_ID:
			Calendar c = Calendar.getInstance();
			return new DatePickerDialog(this, birthdayListener,
					c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}
}
