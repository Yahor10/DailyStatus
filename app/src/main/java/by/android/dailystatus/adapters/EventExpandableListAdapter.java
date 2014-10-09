package by.android.dailystatus.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import by.android.dailystatus.R;
import by.android.dailystatus.orm.model.EventORM;

public class EventExpandableListAdapter extends BaseExpandableListAdapter {

	private ArrayList<ArrayList<EventORM>> mGroups;
	private Context mContext;
	LayoutInflater inflater;

	boolean flagDrawItems = false;
	int day = 0;
	int year = 0;

	public EventExpandableListAdapter(Context context,
			ArrayList<ArrayList<EventORM>> groups) {
		mContext = context;
		mGroups = groups;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int findItemByDate(int dayOfYear, int year1) {
		day = dayOfYear;
		this.year = year1;
		flagDrawItems = true;
		for (ArrayList<EventORM> events : mGroups) {

			for (int i = 0; i < events.size(); i++) {

				if (events.get(i).day == day && events.get(i).year == year) {
					return i;
				}

			}
		}

		return -1;
	}
	
	public void cleanFlagDrawItems() {
		flagDrawItems = false;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mGroups.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		HolderChild holder;
		if (convertView == null) {
			holder = new HolderChild();
			convertView = inflater.inflate(R.layout.item_event_list, null);
			convertView.setTag(holder);

			holder.describeTest = (TextView) convertView
					.findViewById(R.id.txt_item_event);

		} else {
			holder = (HolderChild) convertView.getTag();
		}

		if (mGroups.get(groupPosition).get(childPosition).day == day
				&& mGroups.get(groupPosition).get(childPosition).year == year
				&& flagDrawItems == true) {
			holder.describeTest.setTextColor(Color.RED);
		} else {
			holder.describeTest.setTextColor(Color.BLACK);
		}

		holder.describeTest.setText(mGroups.get(groupPosition).get(
				childPosition).description);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (mGroups.get(groupPosition) == null)
			return 0;

		return mGroups.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		if (mGroups == null)
			return 0;
		return mGroups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		HolderGroup holder;
		if (convertView == null) {
			holder = new HolderGroup();
			convertView = inflater.inflate(R.layout.item_group_event_exp_list, null);
			convertView.setTag(holder);

			holder.firstChar = (TextView) convertView
					.findViewById(R.id.txt_first_char);

		} else {
			holder = (HolderGroup) convertView.getTag();
		}

		holder.firstChar.setText(""+ mGroups.get(groupPosition).get(0).description
				.toCharArray()[0]);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public class HolderChild {
		TextView describeTest;

	}

	public class HolderGroup {
		TextView firstChar;

	}

}
