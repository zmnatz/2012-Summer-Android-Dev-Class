package mnatzakanian.zaven.hw3.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
	public static final String ID = "_id";
	public static final String DISPLAY_NAME = "displayName";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String BIRTHDAY = "birthday";
	public static final String HOME_PHONE = "homePhone";
	public static final String WORK_PHONE = "workPhone";
	public static final String MOBILE_PHONE = "mobilePhone";
	public static final String EMAIL_ADDRESS = "emailAddress";

	public static final String[] ALL_FIELDS = new String[] { ID, DISPLAY_NAME, FIRST_NAME, LAST_NAME, BIRTHDAY, HOME_PHONE,
			WORK_PHONE, MOBILE_PHONE, EMAIL_ADDRESS };
	public static final String IDENTIFIER_STRING = ID + "=?";
	public static final String SORT_FIELD = DISPLAY_NAME;

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MM-dd-yy");
	private long id;
	private String displayName;
	private String firstName;
	private String lastName;
	private Date birthday;
	private String homePhone;
	private String workPhone;
	private String mobilePhone;
	private String emailAddress;

	public Contact(long id, String displayName, String firstName, String lastName, Date birthday, String homePhone,
			String workPhone, String mobilePhone, String emailAddress) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.homePhone = homePhone;
		this.workPhone = workPhone;
		this.mobilePhone = mobilePhone;
		this.emailAddress = emailAddress;
	}

	public Contact(Cursor cursor) {
		this(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), Contact.parseDate(cursor
				.getString(4)), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
	}

	public Contact() {

	}

	public Contact(long id, ContentValues values) {
		this(id, values.getAsString(DISPLAY_NAME), values.getAsString(FIRST_NAME), values.getAsString(LAST_NAME),
				parseDate(values.getAsString(BIRTHDAY)), values.getAsString(HOME_PHONE), values.getAsString(WORK_PHONE),
				values.getAsString(MOBILE_PHONE), values.getAsString(EMAIL_ADDRESS));
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public void setBirthday(String date) throws ParseException {
		setBirthday((date == null || date.length() < 1) ? null : FORMATTER.parse(date));
	}

	/**
	 * @return the homePhone
	 */
	public String getHomePhone() {
		return homePhone;
	}

	/**
	 * @param homePhone
	 *            the homePhone to set
	 */
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	/**
	 * @return the workPhone
	 */
	public String getWorkPhone() {
		return workPhone;
	}

	/**
	 * @param workPhone
	 *            the workPhone to set
	 */
	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	/**
	 * @return the mobilePhone
	 */
	public String getMobilePhone() {
		return mobilePhone;
	}

	/**
	 * @param mobilePhone
	 *            the mobilePhone to set
	 */
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(displayName);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(getBirthdayString());
		dest.writeString(homePhone);
		dest.writeString(workPhone);
		dest.writeString(mobilePhone);
		dest.writeString(emailAddress);
	}

	public String getBirthdayString() {
		return birthday != null ? FORMATTER.format(birthday) : "";
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	public static Date parseDate(String dateString) {
		Date birthday = null;
		try {
			birthday = dateString.length() > 0 ? FORMATTER.parse(dateString) : null;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return birthday;
	}

	public static Parcelable.Creator<Contact> CREATOR = new Creator<Contact>() {
		public Contact createFromParcel(Parcel source) {
			long id = source.readLong();
			String displayName = source.readString();
			String firstName = source.readString();
			String lastName = source.readString();
			Date birthday = parseDate(source.readString());
			String homePhone = source.readString();
			String workPhone = source.readString();
			String mobilePhone = source.readString();
			String email = source.readString();

			return new Contact(id, displayName, firstName, lastName, birthday, homePhone, workPhone, mobilePhone, email);
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};

	public String[] getIdentifyingValues() {
		return new String[] { Long.toString(id) };
	}

	public ContentValues getContentValues() {
		ContentValues content = new ContentValues();
		content.put(ID, id);
		content.put(DISPLAY_NAME, displayName);
		content.put(FIRST_NAME, firstName);
		content.put(LAST_NAME, lastName);
		content.put(BIRTHDAY, getBirthdayString());
		content.put(HOME_PHONE, homePhone);
		content.put(WORK_PHONE, workPhone);
		content.put(MOBILE_PHONE, mobilePhone);
		content.put(EMAIL_ADDRESS, emailAddress);
		return content;
	}
}
