package by.android.dailystatus.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.LocalDate;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.DayORM;
import by.android.dailystatus.orm.model.EventORM;

import com.kanak.emptylayout.EmptyLayout;

public class EventListFragment extends Fragment {

	int typeFragment;
	private View view;
	public ListView list;
	ListAdapter adapter;
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

	public EventListFragment(int type) {
		typeFragment = type;
	}

	public EventListFragment() {

	}

	int filter = 0;

	public void setFilterNews(int filter) {
		this.filter = filter;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.event_fragment, null);
		list = (ListView) view.findViewById(R.id.list_event);
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
		adapter = new ListAdapter(inflater);
		emptyLayout = new EmptyLayout(getActivity(), list);

		// ArrayList<EventORM> events = new ArrayList<EventORM>();
		updateFragment();

		return view;
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

	private class ListAdapter extends BaseAdapter {

		LayoutInflater inflater;

		ArrayList<EventORM> events;

		boolean flagDrawItems = false;
		int day;
		int year;

		public void cleanFlagDrawItems() {
			flagDrawItems = false;
		}

		public int findItemByDate(int dayOfYear, int year1) {
			day = dayOfYear;
			this.year = year1;
			flagDrawItems = true;
			for (int i = 0; i < events.size(); i++) {

				if (events.get(i).day == day && events.get(i).year == year) {
					return i;
				}

			}

			return -1;
		}

		public ListAdapter(LayoutInflater inflater) {
			this.inflater = inflater;
		}

		public void setData(ArrayList<EventORM> tests) {
			this.events = tests;
		}

		@Override
		public int getCount() {
			if (events != null)
				return events.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return events.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = inflater.inflate(R.layout.item_event_list, null);
				convertView.setTag(holder);

				holder.describeTest = (TextView) convertView
						.findViewById(R.id.txt_item_event);

			} else {
				holder = (Holder) convertView.getTag();
			}

			if (events.get(position).day == day
					&& events.get(position).year == year
					&& flagDrawItems == true) {
				holder.describeTest.setTextColor(Color.RED);
			} else {
				holder.describeTest.setTextColor(Color.BLACK);
			}

			holder.describeTest.setText(events.get(position).description);

			return convertView;
		}

		public class Holder {
			TextView describeTest;

		}

	}

	// public static Fragment newInstance(int type, int filter) {
	// return new EventListFragment(type, filter);
	// }
	public static Fragment newInstance(int type) {
		return new EventListFragment(type);
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
				if (result.size() != 0) {
					adapter.setData(result);
					list.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				} else {
					emptyLayout.showEmpty();

				}
			} else {
				emptyLayout.showError();
			}

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			emptyLayout.showLoading();
			emptyLayout.setLoadingMessage("Please wait...");

		}
	}

}
