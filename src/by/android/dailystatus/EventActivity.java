package by.android.dailystatus;

import java.util.Calendar;
import java.util.HashMap;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.DatePicker;
import by.android.dailystatus.fragment.EventListFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;
import com.viewpagerindicator.TabPageIndicator;

public class EventActivity extends SherlockFragmentActivity implements
		OnMenuItemClickListener {

	public static final String WEEK = "Week_day";
	public static final String MONTH = "Month";
	public static final String YEAR = "Year";
	private static final String FROM_CALENDAR = "from_calendar";

	private static final String[] CONTENT = new String[] { "Week", "Month",
			"Year" };
	EventsAdapter adapter;
	ViewPager pager;

	OnDateSetListener myCallBack = new OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			adapter.findEventsByDate(cal.get(Calendar.DAY_OF_YEAR), year);

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.simple_tabs);

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new EventsAdapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		boolean fromCalendar = getIntent()
				.getBooleanExtra(FROM_CALENDAR, false);

		if (fromCalendar) {
			pager.setCurrentItem(1);
		} else {
			pager.setCurrentItem(0);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu subMyProfile = menu.addSubMenu("").setIcon(
				getResources().getDrawable(
						R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark));

		subMyProfile.add(0, 3, Menu.NONE,
				getResources().getString(R.string.show_all_events))
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 4, Menu.NONE,
				getResources().getString(R.string.show_good_events))
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 5, Menu.NONE,
				getResources().getString(R.string.show_bad_events))
				.setOnMenuItemClickListener(this);
		subMyProfile.add(0, 6, Menu.NONE,
				getResources().getString(R.string.show_data_picker))
				.setOnMenuItemClickListener(this);
		subMyProfile.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {

		switch (item.getItemId()) {

		case 3:
			// adapter = new AppsAdapter(getSupportFragmentManager());
			// adapter.showAllEvents();
			// pager.setAdapter(adapter);
			adapter.showAllEvents();

			break;
		case 4:

			// adapter = new AppsAdapter(getSupportFragmentManager());
			adapter.showGoodEvents();
			// pager.setAdapter(adapter);

			break;
		case 5:
			// adapter = new AppsAdapter(getSupportFragmentManager());
			adapter.showBadEvents();
			// pager.setAdapter(adapter);

			break;
		case 6:
			Calendar cal = Calendar.getInstance();
			DatePickerDialog tpd = new DatePickerDialog(this, myCallBack,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
			tpd.show();
			break;
		default:
			break;
		}
		return true;
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

	private class EventsAdapter extends FragmentStatePagerAdapter {
		int filterEvent = 0; // 0 - all, -1 - bad, 1- good

		HashMap<Integer, EventListFragment> fragments;

		public EventsAdapter(FragmentManager fm) {
			super(fm);
			fragments = new HashMap<Integer, EventListFragment>();
		}

		public void showBadEvents() {
			filterEvent = -1;
			updateFragments();
		}

		public void showGoodEvents() {
			filterEvent = 1;
			updateFragments();
		}

		public void showAllEvents() {
			filterEvent = 0;
			updateFragments();
		}

		public void findEventsByDate(int dayOfYear, int year) {
			for (Integer key : fragments.keySet()) {

				EventListFragment fragment = fragments.get(key);
				if (fragment != null) {
					fragment.findEventsByDate(dayOfYear, year);
				}
			}
		}

		public void updateFragments() {
			for (Integer key : fragments.keySet()) {

				EventListFragment fragment = fragments.get(key);
				if (fragment != null) {
					fragment.setFilterNews(filterEvent);
					fragment.updateFragment();
				}
			}

		}

		@Override
		public Fragment getItem(int position) {
			EventListFragment fragment = (EventListFragment) EventListFragment
					.newInstance(position);
			Log.d("BUG", "PAGER position : " + position);

			fragments.put(position, fragment);
			return fragment;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (position >= getCount()) {
				FragmentManager manager = ((Fragment) object)
						.getFragmentManager();
				FragmentTransaction trans = manager.beginTransaction();
				trans.remove((Fragment) object);
				trans.commit();
				// fragments.remove(position);
				Log.d("BUG", "PAGER DESTROY position : " + position);
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

	public static Intent buintIntent(Context context, int week, int month,
			int year) {
		Intent intent = new Intent(context, EventActivity.class);
		intent.putExtra(WEEK, week);
		intent.putExtra(MONTH, month);
		intent.putExtra(YEAR, year);
		return intent;
	}

}
