package by.android.dailystatus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.fragment.MonthFragment;
import by.android.dailystatus.fragment.WeekFragment;
import by.android.dailystatus.fragment.YearFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;

public class ChartsActivity extends SherlockFragmentActivity {

	public static final String WEEK = "Week_day";
	public static final String MONTH = "Month";
	public static final String YEAR = "Year";
	private static final String FROM_CALENDAR = "from_calendar";

	private static final String[] CONTENT = new String[] { "Week", "Month",
			"Year" };

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.simple_tabs);

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		FragmentPagerAdapter adapter = new AppsAdapter(
				getSupportFragmentManager());

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class AppsAdapter extends FragmentPagerAdapter {

		public AppsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return WeekFragment.newInstance();
			case 1:
				return MonthFragment.newInstance();
			case 2:
				return YearFragment.newInstance();
			default:
				return null;
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
		Intent intent = new Intent(context, ChartsActivity.class);
		intent.putExtra(WEEK, week);
		intent.putExtra(MONTH, month);
		intent.putExtra(YEAR, year);
		return intent;
	}

	public static Intent buintIntent(Context context, int week, int month,
			int year, boolean fromCalendar) {
		Intent intent = new Intent(context, ChartsActivity.class);
		intent.putExtra(WEEK, week);
		intent.putExtra(MONTH, month);
		intent.putExtra(YEAR, year);
		intent.putExtra(FROM_CALENDAR, fromCalendar);
		return intent;
	}
}
