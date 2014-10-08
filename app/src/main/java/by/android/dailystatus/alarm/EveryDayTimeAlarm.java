package by.android.dailystatus.alarm;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.DayORM;

public class EveryDayTimeAlarm extends BroadcastReceiver {

	NotificationManager nm;

	@Override
	public void onReceive(Context context, Intent intent) {
		ArrayList<day> days = new ArrayList<EveryDayTimeAlarm.day>();
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int year_1 = year;
		int year_2 = year;
		int year_3 = year;
		int year_4 = year;
		int year_5 = year;
		int year_6 = year;

		int day = calendar.get(Calendar.DAY_OF_YEAR);
		int day_1 = day - 1;
		int day_2 = day - 2;
		int day_3 = day - 3;
		int day_4 = day - 4;
		int day_5 = day - 4;
		int day_6 = day - 4;

		if (day_1 <= 0) {
			year_1--;
			day_1 += 31;
		}
		if (day_2 <= 0) {
			year_2--;
			day_2 += 31;
		}
		if (day_3 <= 0) {
			year_3--;
			day_3 += 31;
		}
		if (day_4 <= 0) {
			year_4--;
			day_4 += 31;
		}
		if (day_5 <= 0) {
			year_5--;
			day_5 += 31;
		}
		if (day_6 <= 0) {
			year_6--;
			day_6 += 31;
		}
		days.add(new day(year, day));
		days.add(new day(year_1, day_1));
		days.add(new day(year_2, day_2));
		days.add(new day(year_3, day_3));
		days.add(new day(year_4, day_4));
		days.add(new day(year_5, day_5));
		days.add(new day(year_6, day_6));
		int badDAys = 0;
		int countNotCheckedDays = 7;
		for (day d : days) {
			if (DayORM.getDay(context, d.day, d.year) != null) {
				countNotCheckedDays--;
				if (DayORM.getDay(context, d.day, d.year).status == -1) {
					badDAys++;
				}

			}
		}

		if (badDAys >= 5) {

			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			CharSequence from = "Dayli alarm";
			CharSequence message = context.getResources().getString(
					R.string.alarm_bad_days_message);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					new Intent(), 0);
			Notification notif = new Notification(
					android.R.drawable.ic_input_add, message,
					System.currentTimeMillis());
			notif.setLatestEventInfo(context, from, message, contentIntent);
			nm.notify(1, notif);
		} else if (countNotCheckedDays >= 5) {

			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			CharSequence from = "Dayli alarm";
			CharSequence message = context.getResources().getString(
					R.string.alarm_empty_days_message);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					new Intent(), 0);
			Notification notif = new Notification(
					android.R.drawable.ic_input_add, message,
					System.currentTimeMillis());
			notif.setLatestEventInfo(context, from, message, contentIntent);
			nm.notify(1, notif);
		}
	}

	public class day {
		public day(int year, int day) {
			this.year = year;
			this.day = day;
		}

		int year;
		int day;
	}
}
