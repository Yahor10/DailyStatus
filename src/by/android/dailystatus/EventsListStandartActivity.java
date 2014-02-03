package by.android.dailystatus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import by.android.dailystatus.fragment.EventStandartListFragment;
import by.android.dailystatus.interfaces.FragmentActivityCallback;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;

public class EventsListStandartActivity extends SherlockFragmentActivity
		implements FragmentActivityCallback {

	public static final String MESSAGE_KEY = "message_key";
	private static final String[] CONTENT = new String[] { "Good", "Bad" };

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
	}

	private class AppsAdapter extends FragmentPagerAdapter {

		public AppsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			EventStandartListFragment frag = (EventStandartListFragment) EventStandartListFragment
					.newInstance(position);
			frag.setFragmentCAllback(EventsListStandartActivity.this);
			Fragment fragment = (EventStandartListFragment) frag;
			return fragment;

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
	public void callToActivity(String str) {

		Intent intent = new Intent();
		if (str == null) {
			intent.putExtra(MESSAGE_KEY, "");
		} else {
			intent.putExtra(MESSAGE_KEY, str);
		}

		setResult(RESULT_OK, intent);
		finish();
	}
}