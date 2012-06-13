package mnatzakanian.zaven.hw2;

import java.util.ArrayList;
import java.util.List;

import mnatzakanian.zaven.hw2.beans.Contact;
import android.app.ListActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsActivity extends ListActivity {
	private static final String CONTACT_PARAM = "contact";

	private List<Contact> contacts = new ArrayList<Contact>();
	private ContactList contactList;
	private Contact contact;

	private class ContactList implements ListAdapter {
		private List<DataSetObserver> dataSetObservers = new ArrayList<DataSetObserver>();

		@Override
		public Contact getItem(int arg0) {
			return contacts.get(arg0);
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
		contactList = new ContactList();
		setListAdapter(contactList);

		Contact contact = new Contact();
		contact.setDisplayName("Name");
		contact.setMobilePhone("Phone");
		addContact(contact);

		contact = null;
		if (savedInstanceState != null)
			contact = savedInstanceState.getParcelable(CONTACT_PARAM);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(CONTACT_PARAM, contact);
	}

	public boolean addContact(Contact contact) {
		boolean success = contacts.add(contact);
		contactList.refresh();
		return success;
	}

	public boolean removeContact(Contact contact) {
		boolean removed = contacts.remove(contact);
		contactList.refresh();
		return removed;
	}

	@Override
	public boolean onPreparePanel(int featureId, View view, Menu menu) {
		Toast.makeText(getApplicationContext(), "Sending Person",
				Toast.LENGTH_SHORT).show();
		return true;
	}

}