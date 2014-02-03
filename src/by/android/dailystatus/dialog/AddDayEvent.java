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

	public AddDayEvent() {

	}

	public AddDayEvent(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public AddDayEvent(MainActivity mainActivity, String startText,
			boolean flagEditEvent) {
		this.mainActivity = mainActivity;
		this.flagEditEvent = flagEditEvent;
		this.startText = startText;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final FragmentActivity activity = getActivity();

		final Dialog dialog = new Dialog(activity);
		// dialog.setTitle(R.string.title_dialog_add_event);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_day_event);

		imageBack = (ImageView) dialog.findViewById(R.id.image);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageBack
				.getLayoutParams();

		DateTime now = mainActivity.getNow();
		int day = now.getDayOfYear();
		int year = now.getYear();
		DayORM dayORM = DayORM.getDay(activity, day, year);
		if (dayORM != null) {

			switch (dayORM.status) {
			case 0:
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.setMargins(60, 0, 0, 0);

				break;

			case -1:
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.setMargins(0, 0, 60, 0);
				imageBack.setBackgroundResource(R.drawable.cloud);

				break;

			case 1:
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				params.setMargins(100, 0, 100, 0);
				imageBack.setBackgroundResource(R.drawable.sun);
				break;

			default:
				break;
			}
		} else {
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.setMargins(60, 0, 0, 0);
		}

		imageBack.setLayoutParams(params);
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

				EventORM event = new EventORM(currentUser, day, month, year, 1,
						eventText.getText().toString());
				if (flagEditEvent) {
					EventORM.deleteEventByName(mainActivity, startText,
							event.day);
				}

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
}
