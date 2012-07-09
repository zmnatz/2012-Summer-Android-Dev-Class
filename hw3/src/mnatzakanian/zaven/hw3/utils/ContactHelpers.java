package mnatzakanian.zaven.hw3.utils;

import mnatzakanian.zaven.hw3.EditContactActivity;
import mnatzakanian.zaven.hw3.beans.Contact;
import mnatzakanian.zaven.hw3.provider.ContactListProvider;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/**
 * Collection of static methods to make common code uniform
 */
public final class ContactHelpers {
	public static final String CONTACT_PARAM = "contact";
	public static final int NEW_CONTACT_ID = -1;

	private ContactHelpers() {
	}

	/**
	 * Create an activity to edit a contact with the given ID
	 * 
	 * @param activity Current activity trying to create the Edit Activity
	 * @param id ID of the contact to edit (-1 if new Contact)
	 */
	public static void editContact(Activity activity, long id) {
		Intent intent = new Intent(activity.getApplicationContext(), EditContactActivity.class);
		intent.putExtra(CONTACT_PARAM, id);
		activity.startActivity(intent);
	}

	/**
	 * Load a contact from the Database
	 * 
	 * @param activity Current activity trying to load the contact
	 * @param id ID of the contact to load, will return null if NEW_CONTACT_ID
	 *            is sent
	 * @return Contact object loaded from the database
	 */
	public static Contact loadContact(Activity activity) {
		long id = extractIdFromActivity(activity);
		Contact contact = null;
		if (id != NEW_CONTACT_ID) {
			Cursor cursor = activity.getContentResolver().query(
					Uri.withAppendedPath(ContactListProvider.CONTENT_URI, id + ""), Contact.ALL_FIELDS, null, null,
					null);
			try {
				if (cursor.moveToFirst()) {
					return new Contact(cursor);
				}
			} finally {
				cursor.close();
			}
		}
		return contact;
	}

	/**
	 * Extract the relevant contact ID from the intent of an activity
	 * 
	 * @param activity Current Activity
	 * @return The long value of the ID currently stored in the activity's
	 *         intent. Or NEW_CONTACT_ID if no value is stored
	 */
	public static long extractIdFromActivity(Activity activity) {
		return activity.getIntent().getLongExtra(CONTACT_PARAM, NEW_CONTACT_ID);
	}
}
