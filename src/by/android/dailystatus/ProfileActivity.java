package by.android.dailystatus;

import java.util.List;

import org.joda.time.DateTime;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import by.android.dailystatus.orm.model.DayORM;
import by.android.dailystatus.orm.model.UserORM;
import by.android.dailystatus.preference.PreferenceUtils;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ProfileActivity extends SherlockActivity {

	ImageView imgProfile;
	TextView txtProfile;
	TextView txtName;
	TextView txtGoodDay;
	TextView txtBadDay;


	UserORM currentUser;
	int goodDayCount, badDayCount;
	private DateTime date;

	private LayoutInflater inflater;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		inflater = getLayoutInflater();
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		date = DateTime.now();

		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setResult(RESULT_OK, new Intent());

				finish();

			}
		});

		initial();

		setData();
		// setContent((TextView)findViewById(R.id.text));
	}

	private void initial() {

		imgProfile = (ImageView) findViewById(R.id.imgProfile);
		txtProfile = (TextView) findViewById(R.id.txtProfile);
		txtName = (TextView) findViewById(R.id.textView1);
		txtGoodDay = (TextView) findViewById(R.id.textView3);
		txtBadDay = (TextView) findViewById(R.id.textView4);

		String currentName = PreferenceUtils
				.getCurrentUser(getApplicationContext());
		currentUser = UserORM.getUserByName(this, currentName);

		List<DayORM> badDays = DayORM.getBadDaysByYear(getApplicationContext(),
				date.getYear());
		if (badDays != null) {
			badDayCount = badDays.size();
		}

		List<DayORM> goodDays = DayORM.getGoodDaysByYear(
				getApplicationContext(), date.getYear());
		if (goodDays != null) {
			goodDayCount = goodDays.size();
		}

	}

	private void setData() {
		if (currentUser != null) {
			txtProfile.setText(currentUser.name + " " + currentUser.lastName);
			txtName.setText(currentUser.name);

		}

		txtBadDay.setText("" + badDayCount);
		txtGoodDay.setText("" + goodDayCount);

	}

	/*
	 * protected void setContent(TextView view) {
	 * view.setText(R.string.action_items_content); }
	 */

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
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
		super.onBackPressed();
	}

}
