package by.android.dailystatus.alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import by.android.dailystatus.MainActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.DayORM;

public class TimeAlarm extends BroadcastReceiver {

	NotificationManager nm;

	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar calendar = Calendar.getInstance();
		if (DayORM.getDay(context, calendar.get(Calendar.DAY_OF_YEAR),
				calendar.get(Calendar.YEAR)) == null) {

			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			CharSequence message = context.getResources().getString(
					R.string.alarm_message);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setSmallIcon(R.drawable.ic_launcher).setContentText(message).setTicker(message).setWhen(System.currentTimeMillis())
					.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));
			nm.notify(1, builder.build());
		}
	}
}
