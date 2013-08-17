package by.android.dailystatus.dialog;

import by.android.dailystatus.R;
import by.android.dailystatus.widget.container.EventLayout;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class AddDayEvent extends DialogFragment implements OnClickListener {


	public AddDayEvent() {
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final FragmentActivity activity = getActivity();

		final Dialog dialog = new Dialog(activity);
		dialog.setTitle("Add day event");
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
			// save event To db and update layout
			break;
		case R.id.addEventCancel:
			getDialog().dismiss();
			break;
		default:
			break;
		}

	}
}
