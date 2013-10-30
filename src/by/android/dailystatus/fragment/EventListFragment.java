package by.android.dailystatus.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.LocalDate;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.MainActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.EventORM;

import com.kanak.emptylayout.EmptyLayout;

public class EventListFragment extends Fragment {
	public static final String WEEK = "Week_day";
	public static final String MONTH = "Month";
	public static final String YEAR = "Year";

	int typeFragment;
	private View view;
	public ListView list;
	ListAdapter adapter;
	EmptyLayout emptyLayout;

	public void setTypeFragment(int type) {
		this.typeFragment = type;
	}

	public EventListFragment(int type) {
		typeFragment = type;

	}

	public EventListFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.event_fragment, null);
		list = (ListView) view.findViewById(R.id.list_event);
		adapter = new ListAdapter(inflater);
		emptyLayout = new EmptyLayout(getActivity(), list);

		// ArrayList<EventORM> events = new ArrayList<EventORM>();
		new LoadDBAsync(typeFragment).execute();

		return view;
	}

	private class ListAdapter extends BaseAdapter {

		LayoutInflater inflater;

		ArrayList<EventORM> events;

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

			holder.describeTest.setText(events.get(position).description);

			return convertView;
		}

		public class Holder {
			TextView describeTest;

		}

	}

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
						List<EventORM> event = EventORM.getEventsByDay(
								applicationContext, plusDays.getDayOfYear(),
								year);
						if (event != null) {
							for (EventORM eventORM : event) {
								events.add(eventORM);
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
						List<EventORM> event = EventORM.getEventsByDay(
								applicationContext, plusDays.getDayOfYear(),
								year);
						if (event != null) {
							for (EventORM eventORM : event) {
								events.add(eventORM);
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
						List<EventORM> event = EventORM.getEventsByDay(
								applicationContext, plusDays.getDayOfYear(),
								year);
						if (event != null) {
							for (EventORM eventORM : event) {
								events.add(eventORM);
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
