package mnatzakanian.zaven.hw3.provider;

import mnatzakanian.zaven.hw3.beans.Contact;
import mnatzakanian.zaven.hw3.dao.ContactDataManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ContactListProvider extends ContentProvider {
	/** The contacts database manager */
	private ContactDataManager contactDataManager;

	// define constants for our URI processing and mime types
	// Courtesy of Stanchfield Todo Provider
	public static final String AUTHORITY = "mnatzakanian.zaven.hw3.contacts";
	public static final int CONTACTS = 1;
	public static final int CONTACT_ENTRY = 2;
	public static final String BASE_PATH = "contacts";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mnatzakanian-contact";
	public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/mnatzakanian-contact";

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH, CONTACTS);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/#", CONTACT_ENTRY);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (URI_MATCHER.match(uri)) {
		case CONTACTS:
			return contactDataManager.readAll();
		case CONTACT_ENTRY:
			String id = uri.getLastPathSegment();
			return contactDataManager.getContactReadCursor(Contact.IDENTIFIER_STRING, id);
		default:
			throw new IllegalArgumentException("Bad URI: " + uri);
		}
	}

	// End of Code from Stanchfield

	@Override
	public boolean onCreate() {
		contactDataManager = new ContactDataManager(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case CONTACTS:
			return CONTENT_DIR_TYPE;
		case CONTACT_ENTRY:
			return CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Bad URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return Uri.parse(CONTENT_URI + "/" + contactDataManager.create(values));
	}

	/**
	 * Attempt to parse the URI for a specific contact ID, if none present, use
	 * selection and selectionArgs to try to execute a delete
	 **/
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		try {
			String idString = uri.getLastPathSegment();
			return contactDataManager.delete(Long.parseLong(idString));
		} catch (NumberFormatException e) {
			return contactDataManager.delete(selection, selectionArgs);
		}
	}

	/**
	 * Attempt to parse the URI for a specific contact ID, if none present, use
	 * selection and selectionArgs to try to execute an update
	 **/
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		try {
			String id = uri.getLastPathSegment();
			values.put(Contact.ID, Long.parseLong(id));
			return contactDataManager.update(values);
		} catch (NumberFormatException e) {
			return contactDataManager.update(values, selection, selectionArgs);
		}
	}
}
