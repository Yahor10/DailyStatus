package by.android.dailystatus;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;
import by.android.dailystatus.widget.calendar.Utils;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {

	private static final String[] DATA = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };

	private static final int DAY_OF_WEEK_LABEL_IDS[] = { R.id.day0, R.id.day1,
			R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6 };
	private static final int DAY_OF_WEEK_KINDS[] = { Calendar.SUNDAY,
			Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
			Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY };

	private int startDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));

		// Get first day of week based on locale and populate the day headers
		startDay = Calendar.getInstance().getFirstDayOfWeek();
		final int startDay = Utils.getFirstDayOfWeek();
		final int sundayColor = getResources().getColor(
				R.color.sunday_text_color);
		final int saturdayColor = getResources().getColor(
				R.color.saturday_text_color);
		
		DateFormatSymbols symbols = new DateFormatSymbols(new Locale("EN"));
		String[] dayNames = symbols.getShortWeekdays();
		for(int i =0 ; i < dayNames.length -1 ;i++){
			dayNames[i] = dayNames[i + 1];
		}
			
		for (int day = 0; day < 7; day++) {
			final String dayString = dayNames[day];
			final TextView label = (TextView) findViewById(DAY_OF_WEEK_LABEL_IDS[day]);
			label.setText(dayString);
			if (Utils.isSunday(day, startDay)) {
				label.setTextColor(sundayColor);
			} else if (Utils.isSaturday(day, startDay)) {
				label.setTextColor(saturdayColor);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar

		menu.add("Save").setIcon(R.drawable.ic_compose)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}

}
