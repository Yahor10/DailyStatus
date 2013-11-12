package by.android.dailystatus.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import by.android.dailystatus.R;
import by.android.dailystatus.interfaces.FragmentActivityCallback;

import com.kanak.emptylayout.EmptyLayout;

public class EventStandartListFragment extends Fragment {

	int typeFragment; // 0 -- bad, 1--good
	private View view;
	public ListView list;
	ListAdapter adapter;
	EmptyLayout emptyLayout;
	String[] mTestArray;

	public static Fragment newInstance(int type) {
		return new EventStandartListFragment(type);
	}

	public EventStandartListFragment(int type) {
		typeFragment = type;

	}

	public EventStandartListFragment() {

	}

	FragmentActivityCallback callback;

	public void setFragmentCAllback(FragmentActivityCallback container) {
		callback = container;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.event_fragment, null);
		list = (ListView) view.findViewById(R.id.list_event);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				callback.callToActivity((String) list.getAdapter().getItem(position));
			}
		});

		adapter = new ListAdapter(inflater);
		emptyLayout = new EmptyLayout(getActivity(), list);

		// ArrayList<EventORM> events = new ArrayList<EventORM>();
		new LoadXMLAsync(typeFragment).execute();

		return view;
	}

	private class ListAdapter extends BaseAdapter {

		LayoutInflater inflater;

		String[] events;

		public ListAdapter(LayoutInflater inflater) {
			this.inflater = inflater;
		}

		public void setData(String[] events) {
			this.events = events;
		}

		@Override
		public int getCount() {
			if (events != null)
				return events.length;
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return events[position];
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
				convertView = inflater.inflate(
						R.layout.item_standart_event_list, null);
				convertView.setTag(holder);

				holder.describeTest = (TextView) convertView
						.findViewById(R.id.txt_item_standart_event);

			} else {
				holder = (Holder) convertView.getTag();
			}

			holder.describeTest.setText(events[position]);

			return convertView;
		}

		public class Holder {
			TextView describeTest;

		}

	}

	class LoadXMLAsync extends AsyncTask<Void, Void, String[]> {
		int type;

		public LoadXMLAsync(int type) {
			this.type = type;
		}

		@Override
		protected String[] doInBackground(Void... params) {
			Context applicationContext = getActivity().getApplicationContext();
			String[] result;
			result = getResources().getStringArray(R.array.testArray);
			return result;

		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);

			if (result != null) {
				if (result.length != 0) {
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
