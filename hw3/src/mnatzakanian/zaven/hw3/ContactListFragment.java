package mnatzakanian.zaven.hw3;

import mnatzakanian.zaven.hw3.beans.Contact;
import mnatzakanian.zaven.hw3.dao.ContactDataManager;
import mnatzakanian.zaven.hw3.provider.ContactListProvider;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

public class ContactListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static final String[] ENTRY_FIELDS = Contact.ALL_FIELDS;

	private static final int ENTRY_LAYOUT = R.layout.contact_entry;

	public interface ContactSelectedListener {
		void onContactSelected(long id);
	}

	private static final int CONTACT_LOADER = 1;

	private ContactSelectedListener contactSelectedListener;

	private SimpleCursorAdapter adapter;

	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity().getApplicationContext(), ContactListProvider.CONTENT_URI, ENTRY_FIELDS,
				null, null, ContactDataManager.CONTACT_TABLE + " asc");
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.contactSelectedListener = (ContactSelectedListener) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), ENTRY_LAYOUT, null,
				ENTRY_FIELDS, Contact.RESOURCES, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		contactSelectedListener.onContactSelected(id);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Destroy and re-initialize the contact loader
		getLoaderManager().destroyLoader(CONTACT_LOADER);
		getLoaderManager().initLoader(CONTACT_LOADER, null, this);
	}

}
