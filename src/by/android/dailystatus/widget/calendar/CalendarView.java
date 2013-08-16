package by.android.dailystatus.widget.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.joda.time.LocalDate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.orm.model.DayORM;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;

public class CalendarView extends SherlockActivity implements
		OnMenuItemClickListener {

	public GregorianCalendar month, itemmonth;// calendar instances.

	public CalendarAdapter adapter;// adapter instance
	public Handler handler;// for grabbing some event values for showing the dot
							// marker.
	public ArrayList<String> items; // container to store calendar items which
									// needs showing the event marker

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		Locale.setDefault(Locale.US);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// TODO find correct place for show events button
		month = (GregorianCalendar) GregorianCalendar.getInstance();
		itemmonth = (GregorianCalendar) month.clone();

		items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();
		handler.post(calendarUpdater);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

		RelativeLayout previous = (RelativeLayout) findViewById(R.id.previous);

		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPreviousMonth();
				refreshCalendar();
			}
		});

		RelativeLayout next = (RelativeLayout) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setNextMonth();
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				((CalendarAdapter) parent.getAdapter()).setSelected(v);
				String selectedGridDate = CalendarAdapter.dayString
						.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*",
						"");// taking last part of date. ie; 2 from 2012-12-02.
				int gridvalue = Integer.parseInt(gridvalueString);
				// navigate to next or previous month on clicking offdays.
				if ((gridvalue > 10) && (position < 8)) {
					setPreviousMonth();
					refreshCalendar();
				} else if ((gridvalue < 7) && (position > 28)) {
					setNextMonth();
					refreshCalendar();
				}
				((CalendarAdapter) parent.getAdapter()).setSelected(v);

				showToast(selectedGridDate);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu subMyProfile = menu.addSubMenu("").setIcon(
				getResources().getDrawable(
						R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark));

		subMyProfile.add(0, 4, Menu.NONE, "Show Good days")
				.setIcon(R.drawable.ic_good_day)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 5, Menu.NONE, "Show Bad days")
				.setIcon(R.drawable.ic_bad_day)
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 6, Menu.NONE, "Show month chart")
				.setOnMenuItemClickListener(this);
		subMyProfile.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int itemId = item.getItemId();
		int monthNumber = month.get(Calendar.MONTH);
		int yearNumber = month.get(Calendar.YEAR);
		switch (itemId) {
		case 4:
			List<DayORM> goodDaysByMonth = DayORM.getGoodDaysByMonth(this,
					monthNumber + 1,yearNumber);
			Set<Integer> goodDays = new HashSet<Integer>();
			for (DayORM dayORM : goodDaysByMonth) {
				goodDays.add(dayORM.day);
			}
			adapter.setGoodDays(goodDays);
			adapter.notifyDataSetChanged();
			break;
		case 5:
			List<DayORM> badDaysByMonth = DayORM.getBadDaysByMonth(this,
					monthNumber + 1,yearNumber);
			Set<Integer> badDays = new HashSet<Integer>();
			for (DayORM dayORM : badDaysByMonth) {
				badDays.add(dayORM.day);
			}
			adapter.setBadDays(badDays);
			adapter.notifyDataSetChanged();
			break;
		case 6:
			LocalDate date = new LocalDate(month.getTime());
			int dayOfYear = date.getDayOfYear();
			int monthOfYear = date.getMonthOfYear();
			int year = date.getYear();
			Log.i(Constants.TAG, "DATE" + date);
//			Intent buintIntent = ChartsActivity
//					.buintIntent(this, dayOfYear, monthOfYear, year, true);
//			startActivity(buintIntent);
			// TODO check activity stack
			break;
		default:
			break;
		}
		return true;
	}

	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}

	}

	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}

	}

	protected void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();

	}

	public void refreshCalendar() {
		TextView title = (TextView) findViewById(R.id.title);

		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();

			// Print dates of the current week
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			String itemvalue;
			for (int i = 0; i < 7; i++) {
				itemvalue = df.format(itemmonth.getTime());
				itemmonth.add(GregorianCalendar.DATE, 1);
				items.add("2012-09-12");
				items.add("2012-10-07");
				items.add("2012-10-15");
				items.add("2012-10-20");
				items.add("2012-11-30");
				items.add("2012-11-28");
			}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};

	public static Intent buintIntent(Context context) {
		return new Intent(context, CalendarView.class);
	}
}
