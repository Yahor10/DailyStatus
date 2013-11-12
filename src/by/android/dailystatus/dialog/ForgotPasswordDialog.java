package by.android.dailystatus.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.UserORM;
import by.android.dailystatus.social.email.GMailSender;

public class ForgotPasswordDialog extends DialogFragment implements
		OnClickListener {

	private Context mainActivity;
	ImageView imageBack;
	UserORM user;

	public ForgotPasswordDialog() {

	}

	public ForgotPasswordDialog(Context mainActivity, UserORM user) {
		this.mainActivity = mainActivity;
		this.user = user;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final FragmentActivity activity = getActivity();
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_forgot_pass);

		dialog.findViewById(R.id.btn_cancel_send_password).setOnClickListener(
				this);
		dialog.findViewById(R.id.btn_send_password).setOnClickListener(this);
		return dialog;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_send_password:

			if (user != null) {
				String[] data = { user.password, user.email };
				new SendEmail().execute(data);
			}
			break;
		case R.id.btn_cancel_send_password:
			getDialog().dismiss();
			break;
		default:
			break;
		}

	}

	class SendEmail extends AsyncTask<String, String, Boolean> {

		ProgressDialog progressDialog;
		String email;

		public SendEmail() {
			progressDialog = new ProgressDialog(getActivity());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();

		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			if (result)
				Toast.makeText(getActivity().getApplicationContext(),
						"Email successfully", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getActivity().getApplicationContext(),
						"Email failed", Toast.LENGTH_SHORT).show();
			getDialog().dismiss();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			if (params[0] != null && params[1] != null) {
				try {
					if (params[1] != "") {
						GMailSender sender = new GMailSender(
								STRING-your-EMAIL, PASSWORD);
						sender.sendMail("Ваш пароль DailyStatus", "Passqord:"
								+ "'" + params[0] + "'",
								"alexpers92@gmail.com", "alex-pers92@mail.ru");
						return true;
					} else {
						return false;
					}

				} catch (Exception e) {
					Log.e("SendMail", e.getMessage(), e);
				}
			}

			return false; // To change body of implemented methods use File |
							// Settings | File Templates.

		}
	}
}
