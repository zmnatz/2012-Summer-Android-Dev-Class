package mnatzakanian.zaven.hw3;

import java.util.ArrayList;

import mnatzakanian.zaven.hw3.beans.Contact;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsActivity extends ListActivity {
	private static final int EDIT_CONTACT_REQUEST_ID = 41;

	private static final int DISPLAY_CONTACT_REQUEST_ID = 40;

	public static final String CONTACT_PARAM = "contact";

	private final ContactList contactList = new ContactList();

	private class ContactList extends BaseAdapter {
		public ArrayList<Contact> contacts = new ArrayList<Contact>();
		
		public int getCount() {
			return contacts.size();
		}

		public Contact getItem(int arg0) {
			return contacts.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int itemNum, View view, ViewGroup parentGroup) {
			Contact contact = getItem(itemNum);
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.contact_entry, null);
			}
			TextView displayView = (TextView) view.findViewById(R.id.displayName);
			TextView phoneView = (TextView) view.findViewById(R.id.phoneNumber);
			displayView.setText(contact.getDisplayName());
			phoneView.setText(contact.getMobilePhone());
			return view;
		}
		
		public boolean addContact(Contact contact) {
			contact.setId(contacts.size());
			boolean success = contacts.add(contact);
			notifyDataSetChanged();
			return success;
		}

		public void updateContact(Contact contact) {
			long index = contact.getId();
			if(index == 0)
				
				contacts.set((int) index, contact);
				notifyDataSetChanged();
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(contactList);

		if (savedInstanceState != null)
			contactList.contacts = savedInstanceState.getParcelableArrayList(CONTACT_PARAM);

		if (contactList.isEmpty())
			Toast.makeText(getApplicationContext(), R.string.empty_list_label, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(CONTACT_PARAM, contactList.contacts);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_list_menu, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent displayContact = new Intent("mnatzakanian.zaven.hw2.intents.viewContact");
		displayContact.putExtra(CONTACT_PARAM, contactList.getItem(position));
		startActivityForResult(displayContact, DISPLAY_CONTACT_REQUEST_ID);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case DISPLAY_CONTACT_REQUEST_ID:
				contactList.updateContact((Contact) data.getParcelableExtra(CONTACT_PARAM));
				break;
			case EDIT_CONTACT_REQUEST_ID:
				contactList.addContact((Contact) data.getParcelableExtra(CONTACT_PARAM));
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
			}
		}
	}
}