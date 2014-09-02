package by.android.dailystatus;

import static by.android.dailystatus.application.Constants.TAG;

import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.ViewGroup;
import android.widget.DatePicker;
import by.android.dailystatus.adapters.EventListIndexedAdapter;
import by.android.dailystatus.fragment.EventListFragment;
import by.android.dailystatus.widget.animations.AnimationViewPagerFragmentZoom;

import com.viewpagerindicator.TabPageIndicator;

@SuppressLint("NewApi")
public class EventActivity extends ActionBarActivity implements
		OnMenuItemClickListener, OnPageChangeListener {

	public static final String WEEK = "Week_day";
	public static final String MONTH = "Month";
	public static final String YEAR = "Year";
	private static final String FROM_CALENDAR = "from_calendar";

	EventsAdapter adapter;
	ViewPager pager;
	private SearchView mSearchViewWeek;
	private MenuItem mSearchWeek;

	private SearchView mSearchViewMonth;
	private MenuItem mSearchMonth;

	private SearchView mSearchViewYear;
	private MenuItem mSearchYear;

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

		adapter = new EventsAdapter(this, getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setPageTransformer(true, new AnimationViewPagerFragmentZoom());
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		indicator.setOnPageChangeListener(this);

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

		if (mSearchViewWeek == null) {
			mSearchViewWeek = new SearchView(getSupportActionBar()
					.getThemedContext());
			mSearchViewWeek
					.setQueryHint(getString(R.string.search_week_events));
			mSearchViewWeek.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					EventListFragment fragmentAtPosition = adapter
							.getFragmentAtPosition(0);
					if (fragmentAtPosition != null) {
						EventListIndexedAdapter listAdapter = fragmentAtPosition
								.getAdapter();
						if (listAdapter != null) {
							listAdapter.getFilter().filter(newText);
						} else {
							Log.e(TAG, "listAdapter is null");
						}
					}
					return false;
				}
			});

			if (mSearchWeek == null) {
				mSearchWeek = menu.add(R.string.search_week);
				mSearchWeek
						.setIcon(R.drawable.ic_launcher)
						.setActionView(mSearchViewWeek)
						.setShowAsAction(
								MenuItem.SHOW_AS_ACTION_ALWAYS
										| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			}
		}

		if (mSearchViewMonth == null) {
			mSearchViewMonth = new SearchView(getSupportActionBar()
					.getThemedContext());
			mSearchViewMonth
					.setQueryHint(getString(R.string.search_month_events));
			mSearchViewMonth.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					EventListFragment fragmentAtPosition = adapter
							.getFragmentAtPosition(1);
					if (fragmentAtPosition != null) {
						EventListIndexedAdapter listAdapter = fragmentAtPosition
								.getAdapter();
						if (listAdapter != null) {
							listAdapter.getFilter().filter(newText);
						} else {
							Log.e(TAG, "listAdapter is null");
						}
					}
					return false;
				}
			});

			if (mSearchMonth == null) {
				mSearchMonth = menu.add(R.string.search_month);
				mSearchMonth
						.setIcon(android.R.drawable.ic_menu_search)
						.setActionView(mSearchViewMonth)
						.setShowAsAction(
								MenuItem.SHOW_AS_ACTION_ALWAYS
										| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			}
		}

		if (mSearchViewYear == null) {
			mSearchViewYear = new SearchView(getSupportActionBar()
					.getThemedContext());
			mSearchViewYear
					.setQueryHint(getString(R.string.search_year_events));
			mSearchViewYear.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					EventListFragment fragmentAtPosition = adapter
							.getFragmentAtPosition(2);
					if (fragmentAtPosition != null) {
						EventListIndexedAdapter listAdapter = fragmentAtPosition
								.getAdapter();
						if (listAdapter != null) {
							listAdapter.getFilter().filter(newText);
						} else {
							Log.e(TAG, "listAdapter is null");
						}
					}
					return false;
				}
			});

			if (mSearchYear == null) {
				mSearchYear = menu.add(R.string.search_year);
				mSearchYear
						.setIcon(R.drawable.ic_launcher)
						.setActionView(mSearchViewYear)
						.setShowAsAction(
								MenuItem.SHOW_AS_ACTION_ALWAYS
										| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			}
		}

		SubMenu subMyProfile = menu.addSubMenu(R.string.show_events).setIcon(
				getResources().getDrawable(
						R.drawable.ic_launcher));

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

		int currentItem = pager.getCurrentItem();
		updateSearchView(currentItem);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {

		switch (item.getItemId()) {

		case 3:
			adapter.showAllEvents();
			break;
		case 4:

			adapter.showGoodEvents();

			break;
		case 5:
			adapter.showBadEvents();

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

	private static class EventsAdapter extends FragmentStatePagerAdapter {
		int filterEvent = 0; // 0 - all, -1 - bad, 1- good

		private final HashMap<Integer, EventListFragment> fragments;
		private final Context mContext;

		public EventsAdapter(Context context, FragmentManager fm) {
			super(fm);
			fragments = new HashMap<Integer, EventListFragment>();
			mContext = context;

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
			fragment.setFilterNews(filterEvent);
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
			switch (position) {
			case 0:
				return mContext.getString(R.string.week);
			case 1:
				return mContext.getString(R.string.month);
			case 2:
				return mContext.getString(R.string.year);
			default:
				return "";
			}

		}

		@Override
		public int getCount() {
			return 3;
		}

		public EventListFragment getFragmentAtPosition(int position) {
			return fragments.get(position);
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

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int page) {
		updateSearchView(page);
		collapseSearchMenu();
	}

	@SuppressLint("NewApi")
	private void collapseSearchMenu() {
		try {
			mSearchWeek.collapseActionView();
			mSearchMonth.collapseActionView();
			mSearchYear.collapseActionView();
			mSearchViewWeek.setQuery("", false);
			mSearchViewMonth.setQuery("", false);
			mSearchViewYear.setQuery("", false);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private void updateSearchView(int page) {
		switch (page) {
		case 0:
			enableSearchItem(mSearchWeek);
			disableSearchItem(mSearchMonth);
			disableSearchItem(mSearchYear);
			break;
		case 1:
			enableSearchItem(mSearchMonth);
			disableSearchItem(mSearchWeek);
			disableSearchItem(mSearchYear);
			break;
		case 2:
			enableSearchItem(mSearchYear);
			disableSearchItem(mSearchWeek);
			disableSearchItem(mSearchMonth);
			break;
		default:
			break;
		}
	}

	private void enableSearchItem(MenuItem item) {
		if(item!=null)
		item.setVisible(true);
	}

	private void disableSearchItem(MenuItem item) {
		if(item!=null)
		item.setVisible(false);
	}

}
