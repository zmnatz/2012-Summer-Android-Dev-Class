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

	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private final ContactList contactList = new ContactList();

	private class ContactList implements ListAdapter {
		private final List<DataSetObserver> dataSetObservers = new ArrayList<DataSetObserver>();

		@Override
		public Contact getItem(int index) {
			return contacts.get(index);
		}

		@Override
		public long getItemId(int itemNumber) {
			return itemNumber;
		}

		@Override
		public int getItemViewType(int itemId) {
			return 0;
		}

		@Override
		public View getView(int itemNum, View view, ViewGroup parentGroup) {
			Contact contact = getItem(itemNum);
			if (view == null) {
				view = getLayoutInflater()
						.inflate(R.layout.contact_entry, null);
			}
			TextView displayView = (TextView) view
					.findViewById(R.id.displayName);
			TextView phoneView = (TextView) view.findViewById(R.id.phoneNumber);
			displayView.setText(contact.getDisplayName());
			phoneView.setText(contact.getMobilePhone());
			return view;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isEmpty() {
			return contacts.isEmpty();
		}

		@Override
		public boolean areAllItemsEnabled() {
			for (int i = 0; i < contacts.size(); i++) {
				if (!isEnabled(i))
					return false;
			}
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		@Override
		public int getCount() {
			return contacts.size();
		}

		/** Code provided by Stanchfield on Sakai **/
		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			dataSetObservers.add(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			dataSetObservers.remove(observer);
		}

		public void refresh() {
			for (DataSetObserver observer : dataSetObservers) {
				observer.onChanged();
			}
		}
		/** End Stanchfield Code **/
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(contactList);

		if (savedInstanceState != null) {
			this.contacts = savedInstanceState
					.getParcelableArrayList(CONTACT_PARAM);
		}

		if (contacts.isEmpty())
			Toast.makeText(getApplicationContext(), R.string.empty_list_label,
					Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(CONTACT_PARAM, contacts);
	}

	public boolean addContact(Contact contact) {
		contact.setId(contacts.size());
		boolean success = contacts.add(contact);
		contactList.refresh();
		return success;
	}

	public boolean removeContact(Contact contact) {
		boolean removed = contacts.remove(contact);
		contactList.refresh();
		return removed;
	}

	public void updateContact(Contact contact) {
		contacts.set((int) contact.getId(), contact);
		contactList.refresh();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent displayContact = new Intent(
				"mnatzakanian.zaven.hw2.intents.viewContact");
		displayContact.putExtra(CONTACT_PARAM, contactList.getItem(position));
		startActivityForResult(displayContact, DISPLAY_CONTACT_REQUEST_ID);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.createItem:
			Intent intent = new Intent(getApplicationContext(),
					EditContactActivity.class);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) {
			case DISPLAY_CONTACT_REQUEST_ID:
				updateContact((Contact) data.getParcelableExtra(CONTACT_PARAM));
				break;
			case EDIT_CONTACT_REQUEST_ID:
				addContact((Contact) data.getParcelableExtra(CONTACT_PARAM));
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
			}
		}
	}
}