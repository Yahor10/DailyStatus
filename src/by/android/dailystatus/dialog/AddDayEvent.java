package by.android.dailystatus.dialog;

import org.joda.time.DateTime;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import by.android.dailystatus.MainActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.EventORM;
import by.android.dailystatus.preference.PreferenceUtils;

public class AddDayEvent extends DialogFragment implements OnClickListener {

	private MainActivity mainActivity;

	public AddDayEvent() {

	}

	public AddDayEvent(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final FragmentActivity activity = getActivity();

		final Dialog dialog = new Dialog(activity);
		// dialog.setTitle(R.string.title_dialog_add_event);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_day_event);

		dialog.findViewById(R.id.addEventOK).setOnClickListener(this);
		dialog.findViewById(R.id.addEventCancel).setOnClickListener(this);

		return dialog;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.addEventOK:
			DateTime now = mainActivity.getNow();
			String currentUser = PreferenceUtils.getCurrentUser(mainActivity);
			int day = now.getDayOfYear();
			int month = now.getMonthOfYear();
			int year = now.getYear();
			EditText eventText = (EditText) getDialog().findViewById(
					R.id.eventText);
			EventORM event = new EventORM(currentUser, day, month, year, 1,
					eventText.getText().toString());
			EventORM.insertEvent(mainActivity, event);
			mainActivity.updateContent();
			getDialog().dismiss();
			break;
		case R.id.addEventCancel:
			getDialog().dismiss();
			break;
		default:
			break;
		}

	}
}
