package by.android.dailystatus.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.adapters.EventExpandableListAdapter;
import by.android.dailystatus.adapters.EventListIndexedAdapter;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.model.GroupEvent;
import by.android.dailystatus.orm.model.DayORM;
import by.android.dailystatus.orm.model.EventORM;
import by.android.dailystatus.widget.customlistview.AlphabetIndexerView;
import by.android.dailystatus.widget.customlistview.AlphabetListView;

import com.kanak.emptylayout.EmptyLayout;

public class EventListFragment extends Fragment {

	static String[] suffixesRU =
	// 0 1 2 3 4 5 6 7 8 9
	{ "th", "ое", "ое", "е", "ое", "ое", "ое", "ое", "ое", "ое",
			// 10 11 12 13 14 15 16 17 18 19
			"ое", "ое", "ое", "ое", "ое", "ое", "ое", "ое", "ое", "ое",
			// 20 21 22 23 24 25 26 27 28 29
			"ое", "ое", "ое", "е", "ое", "ое", "ое", "ое", "ое", "ое",
			// 30 31
			"ое", "ое" };

	static String[] suffixesENG =
	// 0 1 2 3 4 5 6 7 8 9
	{ "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
			// 10 11 12 13 14 15 16 17 18 19
			"th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
			// 20 21 22 23 24 25 26 27 28 29
			"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
			// 30 31
			"th", "st" };

	int typeFragment;
	private View view;
	ListView list;
	AlphabetListView frameContainer;
	AlphabetIndexerView indexerView;
	EventListIndexedAdapter adapter;

	EmptyLayout emptyLayout;

	public void setTypeFragment(int type) {
		this.typeFragment = type;
	}

	public int getTypeFragment() {
		return typeFragment;
	}

	// public EventListFragment(int type, int filter) {
	// typeFragment = type;
	// this.filter = filter;
	//
	// }
	public EventListFragment() {

	}

	public EventListFragment(int type) {
		typeFragment = type;
	}

	public int filter = 0;
	
	private final DataSetObserver adapterDataObserver = new DataSetObserver() {
		public void onChanged() {
			emptyLayout.showEmpty();
		};
	};

	public void setFilterNews(int filter) {
		this.filter = filter;
	}

	@Override
	public void onDestroy() {
		Log.d("BUG", "FRAGMENT : " + typeFragment + " DESCTROY");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		Log.d("BUG", "FRAGMENT : " + typeFragment + " PAUSE");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.d("BUG", "FRAGMENT : " + typeFragment + " RESUME");
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.d("BUG", "FRAGMENT : " + typeFragment + " START");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.d("BUG", "FRAGMENT : " + typeFragment + " STOP");
		super.onStop();
		adapter.unregisterDataSetObserver(adapterDataObserver);
	}

	// SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("BUG", "FRAGMENT : " + typeFragment + " CREATE");

		view = inflater.inflate(R.layout.event_fragment, null);

		frameContainer = (AlphabetListView) view.findViewById(R.id.list_event);

		list = frameContainer.getListView();
		indexerView = frameContainer.getIndexer();

		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				adapter.cleanFlagDrawItems();
				adapter.notifyDataSetChanged();

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

		// emptyLayout = new EmptyLayout(getActivity(), list);

		updateFragment();

		return view;
	}

	public void updateFragment() {
		new LoadDBAsync(typeFragment).execute();
	}

	public EventListIndexedAdapter getAdapter() {
		return adapter;
	}

	public void findEventsByDate(int dayOfYear, int year) {
		int item = adapter.findItemByDate(dayOfYear, year);
		if (item >= 0) {
			adapter.notifyDataSetChanged();
			list.setSelection(item);
		}

	}

	public static Fragment newInstance(int type) {
		return new EventListFragment(type);
	}

	public String capitalizeFirstLetter(String original) {
		if (original.length() == 0)
			return original;
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	private void refreshAdapter(ArrayList<GroupEvent> data) {
		if (data != null) {
			if (data.isEmpty()) {
				emptyLayout.showEmpty();
			} else {
				adapter = new EventListIndexedAdapter(getActivity(),
						indexerView, data, list);
				adapter.registerDataSetObserver(adapterDataObserver);
				list.setAdapter(adapter);
				indexerView.setIndexerListener(adapter);
			}
		} else {
			emptyLayout.showError();
		}
	}

	public ArrayList<GroupEvent> divideOnGroup(ArrayList<EventORM> events) {
		if (events == null)
			return null;
		class CustomComparator implements Comparator<EventORM> {
			@Override
			public int compare(EventORM o1, EventORM o2) {

				int day1 = o1.day;
				int day2 = o2.day;
				if (day1 < day2) {
					return -1;
				} else if (day2 > day1) {
					return 1;
				}

				return 0;
			}
		}
		Collections.sort(events, new CustomComparator());

		ArrayList<GroupEvent> groups = new ArrayList<GroupEvent>();

		while (events.size() != 0) {

			switch (typeFragment) {
			case 0:
				int index = events.get(0).day;
				DateTime time = new DateTime(events.get(0).date);
				String nameIndex = time.dayOfWeek().getAsText();
				GroupEvent group = new GroupEvent(index,
						capitalizeFirstLetter(nameIndex));

				for (int i = 0; i < events.size(); i++) {

					if (events.get(i).day == index) {
						group.events.add(events.remove(i));
						i--;
					}

				}
				groups.add(group);

				break;
			case 1:

				int index1 = events.get(0).day;
				DateTime time1 = new DateTime(events.get(0).date);
				String nameIndex1;
				String dayName = capitalizeFirstLetter(time1.dayOfWeek()
						.getAsText());
				if (getResources().getString(R.string.location).contains("ru")) {
					nameIndex1 = time1.getDayOfMonth() + "-"
							+ suffixesRU[time1.getDayOfMonth()] + ", "
							+ dayName;
				} else {
					nameIndex1 = time1.getDayOfMonth() + "-"
							+ suffixesENG[time1.getDayOfMonth()] + ", "
							+ dayName;
				}
				GroupEvent group1 = new GroupEvent(index1,
						capitalizeFirstLetter(nameIndex1));

				for (int i = 0; i < events.size(); i++) {

					if (events.get(i).day == index1) {
						group1.events.add(events.remove(i));
						i--;
					}

				}
				groups.add(group1);

				break;
			case 2:

				int index2 = events.get(0).month;
				DateTime time2 = new DateTime(events.get(0).date);
				DateTimeFormatter formatter2 = DateTimeFormat
						.forPattern("MMMM");
				String nameIndex2 = time2.toString(formatter2);
				GroupEvent group2 = new GroupEvent(index2,
						capitalizeFirstLetter(nameIndex2));

				for (int i = 0; i < events.size(); i++) {

					if (events.get(i).month == index2) {
						group2.events.add(events.remove(i));
						i--;
					}

				}
				groups.add(group2);

				break;

			default:
				break;
			}

		}

		return groups;
	}

	class LoadDBAsync extends AsyncTask<Void, Void, ArrayList<EventORM>> {
		int type;

		public LoadDBAsync(int type) {
			this.type = type;
		}

		@Override
		protected ArrayList<EventORM> doInBackground(Void... params) {
			Context applicationContext = getActivity().getApplicationContext();
			int weekDay = getActivity().getIntent().getIntExtra(
					ChartsActivity.WEEK, 0);

			int month = getActivity().getIntent().getIntExtra(
					ChartsActivity.MONTH, 0);

			int year = getActivity().getIntent().getIntExtra(
					ChartsActivity.YEAR, 0);
			Calendar instance = Calendar.getInstance();
			instance.set(Calendar.DAY_OF_YEAR, weekDay);
			LocalDate date = new LocalDate(instance.getTime());

			ArrayList<EventORM> events = new ArrayList<EventORM>();
			try {

				switch (typeFragment) {
				case 0:

					while (date.getDayOfWeek() != 1) {
						date = date.minusDays(1);
					}

					for (int i = 0; i < 7; i++) {

						LocalDate plusDays = date.plusDays(i);

						DayORM dayORM = DayORM.getDay(applicationContext,
								plusDays.getDayOfYear(), plusDays.getYear());

						int filterDAY = 0;
						if (dayORM != null) {
							filterDAY = dayORM.status;
						}

						if (filter == filterDAY || filter == 0) {
							List<EventORM> event = EventORM.getEventsByDay(
									applicationContext,
									plusDays.getDayOfYear(), year);
							if (event != null) {
								for (EventORM eventORM : event) {
									events.add(eventORM);
								}
							}
						}

					}

					break;
				case 1:

					int sizeMounth = 30;
					if (month == 1 || month == 3 || month == 5 || month == 7
							|| month == 8 || month == 10 || month == 12) {
						sizeMounth = 31;
					} else if (month == 4 || month == 6 || month == 9
							|| month == 11) {
						sizeMounth = 30;
					} else if (month == 2) {
						sizeMounth = 28;
					}

					while (date.getDayOfMonth() != 1) {
						date = date.minusDays(1);
					}

					for (int i = 0; i < sizeMounth; i++) {

						LocalDate plusDays = date.plusDays(i);

						DayORM dayORM = DayORM.getDay(applicationContext,
								plusDays.getDayOfYear(), plusDays.getYear());

						int filterDAY = 0;
						if (dayORM != null) {
							filterDAY = dayORM.status;
						}

						if (filter == filterDAY || filter == 0) {
							List<EventORM> event = EventORM.getEventsByDay(
									applicationContext,
									plusDays.getDayOfYear(), year);
							if (event != null) {
								for (EventORM eventORM : event) {
									events.add(eventORM);
								}
							}
						}

					}

					break;

				case 2:

					while (date.getDayOfYear() != 1) {
						date = date.minusDays(1);
					}

					for (int i = 0; i < 365; i++) {

						LocalDate plusDays = date.plusDays(i);

						DayORM dayORM = DayORM.getDay(applicationContext,
								plusDays.getDayOfYear(), plusDays.getYear());

						int filterDAY = 0;
						if (dayORM != null) {
							filterDAY = dayORM.status;
						}

						if (filter == filterDAY || filter == 0) {
							List<EventORM> event = EventORM.getEventsByDay(
									applicationContext,
									plusDays.getDayOfYear(), year);
							if (event != null) {
								for (EventORM eventORM : event) {
									events.add(eventORM);
								}
							}
						}

					}

					break;

				default:
					break;
				}

			} catch (Exception e) {

				return null;
			}

			return events;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d("BUG", "FRAGMENT : " + typeFragment + " START LOAD");
			String loadingMessage = getString(R.string.loading_message);
			emptyLayout = new EmptyLayout(getActivity(), list);
			emptyLayout.setLoadingMessage(loadingMessage);
			emptyLayout.showLoading();

			String emptyMessage = getString(R.string.empty_message);
			String errorMessage = getString(R.string.error_message);

			emptyLayout.setEmptyMessage(emptyMessage);
			emptyLayout.setErrorMessage(errorMessage);

		}

		@Override
		protected void onPostExecute(ArrayList<EventORM> result) {
			super.onPostExecute(result);
			refreshAdapter(divideOnGroup(result));
		}
	}

}
