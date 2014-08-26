package by.android.dailystatus.dialog;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import by.android.dailystatus.MainActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.DayORM;
import by.android.dailystatus.orm.model.EventORM;
import by.android.dailystatus.preference.PreferenceUtils;

public class AddDayEvent extends DialogFragment implements OnClickListener {

	private MainActivity mainActivity;
	ImageView imageBack;
	String startText;
	boolean flagEditEvent = false;
	EditText eventText;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mainActivity = (MainActivity) activity;
	}
	public AddDayEvent() {

	}

	public AddDayEvent(MainActivity mainActivity, String startText,
			boolean flagEditEvent) {
		this.mainActivity = mainActivity;
		this.flagEditEvent = flagEditEvent;
		this.startText = startText;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(mainActivity);
		// dialog.setTitle(R.string.title_dialog_add_event);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_day_event);

		imageBack = (ImageView) dialog.findViewById(R.id.image);

		DateTime now = mainActivity.getNow();
		int day = now.getDayOfYear();
		int year = now.getYear();
		DayORM dayORM = DayORM.getDay(mainActivity, day, year);
		if (dayORM != null) {

			switch (dayORM.status) {
			case 0:
				break;
			case -1:
				imageBack.setBackgroundResource(R.drawable.ic_cloud);
				break;
			case 1:
				imageBack.setBackgroundResource(R.drawable.ic_sun);
				break;

			default:
				break;
			}
		}

		eventText = (EditText) dialog.findViewById(R.id.eventText);
		if (startText != null) {
			eventText.setText(startText);
		}

		dialog.findViewById(R.id.addEventOK).setOnClickListener(this);
		dialog.findViewById(R.id.addEventCancel).setOnClickListener(this);

		return dialog;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.addEventOK:
			if (eventText.getText().toString().trim().length() != 0) {
				DateTime now = mainActivity.getNow();
				
				
				String currentUser = PreferenceUtils
						.getCurrentUser(mainActivity);
				int day = now.getDayOfYear();
				int month = now.getMonthOfYear();
				int year = now.getYear();
				long date = now.getMillis();
				EventORM event = new EventORM(currentUser, day, month, year, date,
						eventText.getText().toString());
				
				if (flagEditEvent) {
					EventORM.deleteEventByName(mainActivity, startText,
							event.day);
				}
				event.new_item=true;

				EventORM.insertEvent(mainActivity, event);
				mainActivity.updateContent();
				getDialog().dismiss();
			}
			break;
		case R.id.addEventCancel:
			getDialog().dismiss();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mainActivity = null;
	}
}
