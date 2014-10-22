package by.android.dailystatus.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.kanak.emptylayout.EmptyLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import by.android.dailystatus.R;
import by.android.dailystatus.adapters.StandartEventExpandableListAdapter;
import by.android.dailystatus.interfaces.FragmentActivityCallback;

public class EventStandartListFragment extends Fragment {

	int typeFragment; // 0 -- bad, 1--good
	private View view;
	public ExpandableListView list;
	StandartEventExpandableListAdapter adapter;
	EmptyLayout emptyLayout;
	String[] mDaysArray;

	public static Fragment newInstance(int type) {
		return new EventStandartListFragment(type);
	}

    @SuppressLint("ValidFragment")
	public EventStandartListFragment(int type) {
		typeFragment = type;

	}

	public EventStandartListFragment() {

	}

	FragmentActivityCallback callback;

	public void setFragmentCAllback(FragmentActivityCallback container) {
		callback = container;
	}

	public void refreshAdapter(ArrayList<ArrayList<String>> data) {
		adapter = new StandartEventExpandableListAdapter(getActivity(), data);
		list.setAdapter(adapter);
		for (int i = 0; i < adapter.getGroupCount(); i++)
		    list.expandGroup(i);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.event_standart_fragment, null);
		list = (ExpandableListView) view.findViewById(R.id.list_event);
		list.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				callback.callToActivity((String) arg1
						.getTag(R.id.tag_standart_event));
				return false;
			}
		});

		emptyLayout = new EmptyLayout(getActivity(), list);

		// ArrayList<EventORM> events = new ArrayList<EventORM>();
		new LoadXMLAsync(typeFragment).execute();

		return view;
	}

	class LoadXMLAsync extends AsyncTask<Void, Void, String[]> {
		int type;

		public LoadXMLAsync(int type) {
			this.type = type;
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String[] result;
			result = getResources().getStringArray(R.array.goodDaysArray);
			return result;

		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);

			if (result != null) {
				if (result.length != 0) {
					refreshAdapter(divideOnGroup(ArrayToList(result)));
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

	public ArrayList<String> ArrayToList(String[] strings) {
		if (strings == null)
			return null;
		ArrayList<String> result = new ArrayList<String>();
		for (String string : strings) {
			result.add(string);
		}
		return result;
	}

	public static ArrayList<ArrayList<String>> divideOnGroup(
			ArrayList<String> events) {
		if (events == null)
			return null;
		class CustomComparator implements Comparator<String> {
			@Override
			public int compare(String str1, String str2) {
				int char1 = (int) str1.trim().toCharArray()[0];
				int char2 = (int) str2.trim().toCharArray()[0];
				if (char1 < char2) {
					return -1;
				} else if (char2 > char1) {
					return 1;
				}

				return 0;
			}
		}

		Collections.sort(events, new CustomComparator());

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		while (events.size() != 0) {
			ArrayList<String> childs = new ArrayList<String>();

			char group = events.get(0).trim().toCharArray()[0];
			int a = (int) group;

			for (int i = 0; i < events.size(); i++) {

				if (events.get(i).indexOf(group) == 0) {
					childs.add(events.remove(i));
					i--;
				}

			}
			result.add(childs);

		}

		return result;
	}

}
