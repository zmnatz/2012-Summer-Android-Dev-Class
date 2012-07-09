package mnatzakanian.zaven.hw3;

import static mnatzakanian.zaven.hw3.utils.ContactHelpers.loadContact;
import mnatzakanian.zaven.hw3.beans.Contact;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayContactFragment extends Fragment {
	private View viewLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		viewLayout = inflater.inflate(R.layout.display, container, false);
		return viewLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}

	public void refreshView() {
		Contact contact = loadContact(getActivity());
		if (contact != null) {
			viewLayout.setVisibility(View.VISIBLE);
			outputToView(R.id.firstName, contact.getFirstName());
			outputToView(R.id.lastName, contact.getLastName());
			outputToView(R.id.displayName, contact.getDisplayName());
			outputToView(R.id.birthday, contact.getBirthdayString());
			outputToView(R.id.mobileNumber, contact.getMobilePhone());
			outputToView(R.id.homeNumber, contact.getHomePhone());
			outputToView(R.id.workNumber, contact.getWorkPhone());
			outputToView(R.id.emailAddr, contact.getEmailAddress());
		} else {
			this.viewLayout.setVisibility(View.INVISIBLE);
		}
	}

	private void outputToView(int id, String output) {
		((TextView) viewLayout.findViewById(id)).setText(output != null ? output : "");
	}
}
