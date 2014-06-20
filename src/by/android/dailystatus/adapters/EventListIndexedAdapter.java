package by.android.dailystatus.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.kanak.emptylayout.EmptyLayout;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import by.android.dailystatus.R;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.model.GroupEvent;
import by.android.dailystatus.orm.model.EventORM;
import by.android.dailystatus.widget.customlistview.AlphabetIndexerView;

import static by.android.dailystatus.application.Constants.TAG;

public class EventListIndexedAdapter extends ArrayAdapter<Object> implements
		SectionIndexer {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SECTION = 1;

	private ArrayList<Object> groupsAndEvents;
	// private LinkedHashMaGroupEventtring, ArrayList<Object>> mappedObjects;
	private HashMap<GroupEvent, Integer> sectionPositions;
	private SparseArray<GroupEvent> positionsOfSections;
	private ArrayList<GroupEvent> groups;
	private final ArrayList<GroupEvent> originalGroup;
	private Context context;

	private LayoutInflater inflater;
	private AlphabetIndexerView indexer;

	private int commonCount = 0;
	private boolean isInEditMode;

	public ListView listViewRef;
	boolean flagDrawItems = false;

	public EventListIndexedAdapter(Context context,
			AlphabetIndexerView alphaIndexer, ArrayList<GroupEvent> days,
			ListView list) {
		super(context, 0);
		this.context = context;
		this.indexer = alphaIndexer;
		inflater = LayoutInflater.from(context);
		positionsOfSections = new SparseArray<GroupEvent>();
		this.groups = days;
		originalGroup = new ArrayList<GroupEvent>(groups);
		this.listViewRef = list;
		init();
		Log.v(TAG, "CONTRUCTOR INIT" + originalGroup.size());
		
	}

	private void init() {
		groupsAndEvents = new ArrayList<Object>();
		positionsOfSections.clear();
		// if (mappedObjects != null)
		// mappedObjects.clear();
		if (sectionPositions != null)
			sectionPositions.clear();
		// if (usersAndPositions != null)
		// usersAndPositions.clear();
		// mappedObjects = (LinkedHashMap<String, ArrayList<Object>>)
		// mapSectionsWithUsers();
		sectionPositions = updateSectionPositions(positionsOfSections);
		commonCount = (groupsAndEvents == null) ? 0 : groupsAndEvents.size();

	}

	/*
	 * Don't use this method if you're not sure for what it is
	 */
	public ArrayList<Object> getContent() {
		return groupsAndEvents;
	}

	@Override
	public int getPositionForSection(int section) {
		// String key = ((GroupEvent) getSections()[section]).weekdayText;
		int position = sectionPositions
				.get((GroupEvent) getSections()[section]);

		if (listViewRef != null) {
			listViewRef.setSelection(position);
		}

		return section;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		Object[] arr = groups.toArray();
		// Arrays.sort(arr);
		return arr;// "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");//
	}

	@Override
	public int getCount() {
		return commonCount;
	}

	@Override
	public Object getItem(int position) {
		return groupsAndEvents.get(position);
	}

	@Override
	public int getItemViewType(int position) {
		if (positionsOfSections.get(position) != null) {
			return TYPE_SECTION;
		}
		return TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results.count > 0) {
					List<GroupEvent> eventsResult = (List<GroupEvent>) results.values;
					groups.clear();
					groups.addAll(eventsResult);

					init();
					notifyDataSetChanged();
				} else{
					groups.clear();
					init();
					notifyDataSetChanged();
				}
				
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				List<GroupEvent> groupEvent = new ArrayList<GroupEvent>(
						originalGroup);
				
				// TODO check empty filter;
				
				List<GroupEvent> groupEventFilter = new ArrayList<GroupEvent>();
				Iterator<GroupEvent> iterator = groupEvent.iterator();

				while (iterator.hasNext()) {
					GroupEvent next = iterator.next();
					boolean foundEvents = false;
					GroupEvent tempGroup = new GroupEvent(next.numberGroup,
							next.nameGroup);
					ArrayList<EventORM> events = next.events;
					Iterator<EventORM> iterator2 = events.iterator();
					while (iterator2.hasNext()) {
						EventORM eventORM = iterator2.next();
						if (eventORM.description.startsWith(constraint
								.toString())) {
							tempGroup.events.add(eventORM);
							foundEvents = true;
						}
					}
					if (foundEvents) {
						groupEventFilter.add(tempGroup);
					}
				}

				for (GroupEvent groupEvent2 : groupEventFilter) {
					Log.v(TAG, "groupEvent2:" + groupEvent2.events);
				}

				results.values = groupEventFilter;
				results.count = groupEventFilter.size();

				return results;
			}
		};
		return filter;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		if (getItemViewType(position) == TYPE_SECTION) {
			return false;
		}
		return true;
	}

	@Override
	public long getItemId(int position) {
		if (getItemViewType(position) == TYPE_SECTION) {
			return -1;
		}
		return ((EventORM) groupsAndEvents.get(position)).id;
	}

	int day = 0;
	int year = 0;

	public int findItemByDate(int dayOfYear, int year1) {
		day = dayOfYear;
		this.year = year1;
		flagDrawItems = true;

		for (int i = 0; i < groupsAndEvents.size(); i++) {
			if (getItemViewType(i) != TYPE_SECTION) {

				if (((EventORM) groupsAndEvents.get(i)).day == day
						&& ((EventORM) groupsAndEvents.get(i)).year == year) {
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
	public View getView(int position, View convertView, ViewGroup parent) {

		if (getItemViewType(position) == TYPE_SECTION) {
			String text = ((GroupEvent) getItem(position)).nameGroup;
			TextView view;
			if (convertView == null || !(convertView instanceof TextView)) {
				view = new TextView(context);
				view.setTextSize(30);
				view.setPadding(20, 5, 20, 5);
				view.setTextColor(Color.BLACK);
			} else {
				view = (TextView) convertView;
			}
			view.setBackgroundColor(context.getResources().getColor(
					R.color.daynames_background));
			view.setText(text);
			return view;
		}
		EventORM event = (EventORM) getItem(position);

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_event_list, null);
			convertView.setTag(holder);

			holder.describeTest = (TextView) convertView
					.findViewById(R.id.txt_item_event);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (event.day == day && event.year == year && flagDrawItems == true) {
			holder.describeTest.setTextColor(Color.RED);
		} else {
			holder.describeTest.setTextColor(Color.BLACK);
		}

		holder.describeTest.setText(event.description);

		return convertView;
	}

	private String capitalize(String input) {
		if (input == null)
			return null;
		if (input.length() == 1)
			return input.toUpperCase();
		String output = input.substring(0, 1).toUpperCase()
				+ input.substring(1);
		return output;
	}

	public void setEditMode(boolean enable) {
		isInEditMode = enable;
	}

	private HashMap<String, ArrayList<Object>> mapSectionsWithUsers() {
		HashMap<String, ArrayList<Object>> map = null;
		String section = null/* , lastName */, firstName;
		// if (c != null && c.moveToFirst()) {
		// map = new LinkedHashMap<String, ArrayList<Object>>();
		// usersAndPositions = new ArrayList<Object>();
		// do {
		// Day user = new Day();
		// user.Id = c.getLong(0);
		// user.FirstName = firstName = c.getString(1);
		// user.LastName /* = lastName */= c.getString(2);
		// user.UserDate = c.getString(3);
		// user.Shade = c.getString(6);
		// user.ToothNumbers = c.getString(4);
		// user.SmileCode = c.getString(5);
		// user.Modification = c.getString(7);
		// user.Number = c.getInt(8);
		// if (!firstName/* lastName */.substring(0, 1).toUpperCase()
		// .equals(section)) {
		// section = firstName/* lastName */.substring(0, 1)
		// .toUpperCase();
		// }
		// if (map.get(section) == null) {
		// map.put(section, new ArrayList<Object>());
		// usersAndPositions.add(section);
		// }
		// map.get(section).add(user);
		// usersAndPositions.add(user);
		// } while (c.moveToNext());
		// c.moveToFirst();
		// }

		return map;
	}

	private HashMap<GroupEvent, Integer> updateSectionPositions(
			SparseArray<GroupEvent> arr) {
		HashMap<GroupEvent, Integer> indexes = null;
		if (groups != null && groups.size() > 0) {
			int sectionPosition = 0;
			indexes = new HashMap<GroupEvent, Integer>();

			for (GroupEvent key : groups) {
				groupsAndEvents.add(key);
				indexes.put(key, sectionPosition);
				arr.put(sectionPosition, (GroupEvent) key);
				sectionPosition += key.events.size() + 1;
				for (EventORM less : key.events) {
					groupsAndEvents.add(less);
				}
			}

		} else {
			arr = null;
		}
		return indexes;
	}

	private static class ViewHolder {
		TextView describeTest;
	}
}
