package mnatzakanian.zaven.hw3.dao;

import static mnatzakanian.zaven.hw3.beans.Contact.ALL_FIELDS;
import static mnatzakanian.zaven.hw3.beans.Contact.BIRTHDAY;
import static mnatzakanian.zaven.hw3.beans.Contact.DISPLAY_NAME;
import static mnatzakanian.zaven.hw3.beans.Contact.FIRST_NAME;
import static mnatzakanian.zaven.hw3.beans.Contact.HOME_PHONE;
import static mnatzakanian.zaven.hw3.beans.Contact.ID;
import static mnatzakanian.zaven.hw3.beans.Contact.LAST_NAME;
import mnatzakanian.zaven.hw3.beans.Contact;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressLint("ParserError")
public class ContactDataManager {
	public static final String DATABASE_IDENTIFIER = "CONTACT_MANAGER";
	public static final String CONTACT_TABLE = "contacts";

	private final OpenHelper openHelper;

	private final SQLiteDatabase db;

	public ContactDataManager(Context context) {
		openHelper = new OpenHelper(context);
		db = openHelper.getReadableDatabase();
	}

	public void close() {
		db.close();
		openHelper.close();
	}

	public Contact create(ContentValues contentValues) {
		long id = db.insert(CONTACT_TABLE, null, contentValues);
		Contact contact = new Contact(contentValues);
		contact.setId(id);
		return contact;
	}

	public Cursor getContactReadCursor(String selection, String... args) {
		return db.query(CONTACT_TABLE, ALL_FIELDS, selection, args, null, null, Contact.SORT_FIELD + " asc");
	}

	public Contact read(long id) {
		Cursor cursor = getContactReadCursor(Contact.IDENTIFIER_STRING, new String[] { Long.toString(id) });
		return cursor.moveToFirst() ? new Contact(cursor) : null;
	}

	public Cursor readAll() {
		return getContactReadCursor(null);
	}

	public int update(ContentValues content, String selection, String... args) {
		return db.update(CONTACT_TABLE, content, selection, args);
	}

	public int update(ContentValues content) {
		return update(content, Contact.IDENTIFIER_STRING, Contact.extractIdentifiers(content));
	}

	public int delete(String where, String... args) {
		return db.delete(CONTACT_TABLE, where, args);
	}

	public int delete(long id) {
		return delete(Contact.IDENTIFIER_STRING, new String[] { "" + id });
	}

	/** A helper class that creates/upgrades a database */
	private class OpenHelper extends SQLiteOpenHelper {
		private static final int VERSION_NO = 1;
		private static final String DROP_TABLE = "drop table ";
		private static final String CREATE_TABLE = "create table ";

		public OpenHelper(Context context) {
			super(context, DATABASE_IDENTIFIER, null, VERSION_NO);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder createTableString = new StringBuilder();
			createTableString.append(CREATE_TABLE).append(CONTACT_TABLE);
			createTableString.append(" (").append(ID).append(" integer primary key autoincrement, ");
			for (String textField : new String[] { DISPLAY_NAME, FIRST_NAME, LAST_NAME, BIRTHDAY, HOME_PHONE,
					Contact.WORK_PHONE, Contact.MOBILE_PHONE, Contact.EMAIL_ADDRESS }) {
				createTableString.append(textField).append(" text, ");
			}
			createTableString.setLength(createTableString.length() - 2);
			createTableString.append(")");
			db.execSQL(createTableString.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DROP_TABLE + CONTACT_TABLE);
			onCreate(db);
		}
	}
}
