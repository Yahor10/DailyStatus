package by.android.dailystatus.alarm;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import by.android.dailystatus.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

public class AlarmActivity extends SherlockFragmentActivity {

	AlarmManager am;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		setOneTimeAlarm();
	}

	public void setOneTimeAlarm() {
		Intent intent = new Intent(this, TimeAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 100),
				pendingIntent);
	}

	public static void setRepeatingAlarm(Context context, int indexOfInterval) {

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, TimeAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				(10800000) + indexOfInterval * 1, pendingIntent);
	}

	public static void CancelAlarm(Context context) {
		Intent intent = new Intent(context, TimeAlarm.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
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

}