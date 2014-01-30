package by.android.dailystatus.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.LocalDate;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.adapters.EventExpandableListAdapter;
import by.android.dailystatus.orm.model.DayORM;
import by.android.dailystatus.orm.model.EventORM;

import com.kanak.emptylayout.EmptyLayout;

public class EventListFragment extends Fragment {

	int typeFragment;
	private View view;
	public ExpandableListView list;
	EventExpandableListAdapter adapter;
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
	}

	// SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("BUG", "FRAGMENT : " + typeFragment + " CREATE");

		view = inflater.inflate(R.layout.event_fragment, null);

		list = (ExpandableListView) view.findViewById(R.id.list_event);

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

	public void refreshAdapter(ArrayList<ArrayList<EventORM>> data) {
		adapter = new EventExpandableListAdapter(getActivity(), data);
		list.setAdapter(adapter);
	}

	public void updateFragment() {
		new LoadDBAsync(typeFragment).execute();
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

	public static ArrayList<ArrayList<EventORM>> divideOnGroup(
			ArrayList<EventORM> events) {
		if (events == null)
			return null;
		class CustomComparator implements Comparator<EventORM> {
			@Override
			public int compare(EventORM o1, EventORM o2) {
				int char1 = (int) o1.description.trim().toCharArray()[0];
				int char2 = (int) o2.description.trim().toCharArray()[0];
				if (char1 < char2) {
					return -1;
				} else if (char2 > char1) {
					return 1;
				}

				return 0;
			}
		}

		Collections.sort(events, new CustomComparator());

		ArrayList<ArrayList<EventORM>> result = new ArrayList<ArrayList<EventORM>>();

		while (events.size() != 0) {
			ArrayList<EventORM> childs = new ArrayList<EventORM>();

			char group = events.get(0).description.trim().toCharArray()[0];
			int a = (int) group;

			for (int i = 0; i < events.size(); i++) {

				if (events.get(i).description.indexOf(group) == 0) {
					childs.add(events.remove(i));
					i--;
				}

			}
			result.add(childs);

		}

		return result;
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
		protected void onPostExecute(ArrayList<EventORM> result) {
			super.onPostExecute(result);

			if (result != null) {
				Log.d("BUG", "FRAGMENT : " + typeFragment + " Size result"
						+ result.size());
				if (result.size() != 0) {
					// adapter.setData(result);
					refreshAdapter(divideOnGroup(result));
					// swingBottomInAnimationAdapter = new
					// SwingBottomInAnimationAdapter(
					// adapter);
					// swingBottomInAnimationAdapter.setAbsListView(list);

					// list.setAdapter(swingBottomInAnimationAdapter);
					adapter.notifyDataSetChanged();
				} else {
					emptyLayout.showEmpty();

				}
			} else {
				Log.d("BUG", "FRAGMENT : " + typeFragment
						+ " Size result = NULL");
				emptyLayout.showError();
			}
			Log.d("BUG",
					"FRAGMENT : " + typeFragment + " Size LIST"
							+ list.getCount());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d("BUG", "FRAGMENT : " + typeFragment + " START LOAD");
			emptyLayout = new EmptyLayout(getActivity(), list);
			emptyLayout.showLoading();
			emptyLayout.setLoadingMessage("Please wait...");

		}
	}

}
