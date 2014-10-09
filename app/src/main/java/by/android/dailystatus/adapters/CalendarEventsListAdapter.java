package by.android.dailystatus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import by.android.dailystatus.R;
import by.android.dailystatus.model.CalendarEvent;

public class CalendarEventsListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;

    private List<List<CalendarEvent>> items;

    private List<String> dates;

    public CalendarEventsListAdapter(Context context, List<List<CalendarEvent>> items, List<String> dates) {
        this.items = items;
        this.dates = dates;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return items.get(i).size();
    }

    @Override
    public List<CalendarEvent> getGroup(int i) {
        return items.get(i);
    }

    @Override
    public CalendarEvent getChild(int i, int i2) {
        return items.get(i).get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_group_event_exp_list, null);
            convertView.setTag(holder);

            holder.title = (TextView) convertView.findViewById(R.id.txt_first_char);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(dates.get(i));

        return convertView;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_event_list, null);
            convertView.setTag(holder);

            holder.title = (TextView) convertView
                    .findViewById(R.id.txt_item_event);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(getChild(i, i2).getTitle());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    class ViewHolder {
        TextView title;
    }
}
