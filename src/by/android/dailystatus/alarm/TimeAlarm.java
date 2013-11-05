package by.android.dailystatus.alarm;

import by.android.dailystatus.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeAlarm extends BroadcastReceiver {

	NotificationManager nm;

	@Override
	public void onReceive(Context context, Intent intent) {
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence from = "Dayli alarm";
		CharSequence message = context.getResources().getString(
				R.string.alarm_message);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(), 0);
		Notification notif = new Notification(android.R.drawable.ic_input_add,
				message, System.currentTimeMillis());
		notif.setLatestEventInfo(context, from, message, contentIntent);
		nm.notify(1, notif);
	}
}
