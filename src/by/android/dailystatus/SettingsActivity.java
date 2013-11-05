package by.android.dailystatus;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import by.android.dailystatus.alarm.AlarmActivity;
import by.android.dailystatus.preference.PreferenceUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tjeannin.apprate.AppRate;

public class SettingsActivity extends SherlockFragmentActivity implements
		OnClickListener {

	RadioGroup radioGroup;
	TextView versionTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		radioGroup = (RadioGroup) findViewById(R.id.rad_group);
		int index = PreferenceUtils
				.getCurrentRadioNotification(getApplicationContext());
		int id = R.id.radbtn_do_not_notify;
		switch (index) {
		case 0:
			id = R.id.radbtn_six_hours;
			break;
		case 2:
			id = R.id.radbtn_three_hours;
			break;

		default:
			break;
		}
		radioGroup.check(id);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				View radioButton = radioGroup.findViewById(checkedId);
				int idx = radioGroup.indexOfChild(radioButton);

				PreferenceUtils.setCurrentRadioNotification(
						getApplicationContext(), idx);

				switch (idx) {
				case 0:

				case 2:
					AlarmActivity.setRepeatingAlarm(getApplicationContext(),
							idx);

					break;
				case 4:
					AlarmActivity.CancelAlarm(getApplicationContext());

					break;

				default:
					break;
				}

			}
		});

		PackageInfo pInfo;
		String vers = "";
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			vers = pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		versionTextView = (TextView) findViewById(R.id.txt_version_description);
		versionTextView.setText(vers);

		findViewById(R.id.lay_rate).setOnClickListener(this);
		findViewById(R.id.lay_connect_with_developer).setOnClickListener(this);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.lay_rate:
			new AppRate(this).setShowIfAppHasCrashed(false).init();

			break;

		case R.id.lay_connect_with_developer:

			String recepientEmail = "sekt88@gmail.com"; // either set to
														// destination
			// email or leave empty
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:" + recepientEmail));

			startActivity(Intent.createChooser(intent, "Send Email"));

			break;

		default:
			break;
		}

	}

}
