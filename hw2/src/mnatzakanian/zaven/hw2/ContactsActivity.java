package mnatzakanian.zaven.hw2;

import java.util.ArrayList;
import java.util.List;

import mnatzakanian.zaven.hw2.beans.Contact;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsActivity extends ListActivity {
	private static final int EDIT_CONTACT_REQUEST_ID = 41;

	private static final int DISPLAY_CONTACT_REQUEST_ID = 40;

	public static final String CONTACT_PARAM = "contact";

	private final ContactList contactList = new ContactList();

	public class ContactList implements ListAdapter {
		public ArrayList<Contact> contacts = new ArrayList<Contact>();
		private final List<DataSetObserver> dataSetObservers = new ArrayList<DataSetObserver>();

		
		public Contact getItem(int index) {
			return contacts.get(index);
		}

		
		public long getItemId(int itemNumber) {
			return itemNumber;
		}

		
		public int getItemViewType(int itemId) {
			return 0;
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

		
		public int getViewTypeCount() {
			return 1;
		}

		
		public boolean hasStableIds() {
			return true;
		}

		
		public boolean isEmpty() {
			return contacts.isEmpty();
		}

		
		public boolean areAllItemsEnabled() {
			for (int i = 0; i < contacts.size(); i++) {
				if (!isEnabled(i))
					return false;
			}
			return true;
		}

		
		public boolean isEnabled(int position) {
			return true;
		}

		
		public int getCount() {
			return contacts.size();
		}

		/** Code provided by Stanchfield on Sakai **/
		
		public void registerDataSetObserver(DataSetObserver observer) {
			dataSetObservers.add(observer);
		}

		
		public void unregisterDataSetObserver(DataSetObserver observer) {
			dataSetObservers.remove(observer);
		}

		public void refresh() {
			for (DataSetObserver observer : dataSetObservers) {
				observer.onChanged();
			}
		}

		/** End Stanchfield Code **/

		public boolean addContact(Contact contact) {
			contact.setId(contacts.size());
			boolean success = contacts.add(contact);
			refresh();
			return success;
		}

		public void updateContact(Contact contact) {
			long index = contact.getId();
			if (contacts.size() > index) {
				contacts.set((int) index, contact);
				refresh();
			}
		}
	}

	/** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(contactList);

		if (savedInstanceState != null)
			contactList.contacts = savedInstanceState.getParcelableArrayList(CONTACT_PARAM);

		if (contactList.isEmpty())
			Toast.makeText(getApplicationContext(), R.string.empty_list_label, Toast.LENGTH_SHORT).show();
	}

	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(CONTACT_PARAM, contactList.contacts);
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_list_menu, menu);
		return true;
	}

	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent displayContact = new Intent("mnatzakanian.zaven.hw2.intents.viewContact");
		displayContact.putExtra(CONTACT_PARAM, contactList.getItem(position));
		startActivityForResult(displayContact, DISPLAY_CONTACT_REQUEST_ID);
	}

	
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