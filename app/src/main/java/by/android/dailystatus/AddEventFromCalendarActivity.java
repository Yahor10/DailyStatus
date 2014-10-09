package by.android.dailystatus;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import by.android.dailystatus.adapters.CalendarEventsListAdapter;
import by.android.dailystatus.model.CalendarEvent;

public class AddEventFromCalendarActivity extends ActionBarActivity
        implements ExpandableListView.OnChildClickListener {

    public static final String MESSAGE_KEY = "message_key";

    private CalendarEventsListAdapter eventsAdapter;
    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_from_calendar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ExpandableListView) findViewById(R.id.events_list);
        listView.setOnChildClickListener(this);

        fillAdapter("");
    }

    private void fillAdapter(String query) {
        TreeMap<String, List<CalendarEvent>> eventsHash = new TreeMap<String, List<CalendarEvent>>();
        Cursor cursor = getContentResolver()
                .query(Uri.parse("content://com.android.calendar/events"),
                        new String[]{"title", "dtstart"},
                        TextUtils.isEmpty(query) ? null : "title like '%" + query + "%'", null, null);
        if (!cursor.moveToFirst()) {
            return;
        }

        do {
            String title = cursor.getString(0);
            Long date = Long.valueOf(cursor.getString(1));

            CalendarEvent event = new CalendarEvent();
            event.setTitle(title);
            event.setDate(date);

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(date);
            String dateString = calendar.get(Calendar.YEAR) + ", "
                    + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

            if (eventsHash.containsKey(dateString)) {
                eventsHash.get(dateString).add(event);
            } else {
                List<CalendarEvent> dateEvents = new ArrayList<CalendarEvent>();
                dateEvents.add(event);
                eventsHash.put(dateString, dateEvents);
            }
        } while (cursor.moveToNext());

        List<List<CalendarEvent>> events = new ArrayList<List<CalendarEvent>>(eventsHash.values());
        List<String> dates = new ArrayList<String>(eventsHash.keySet());
        Collections.reverse(events);
        Collections.reverse(dates);

        eventsAdapter = new CalendarEventsListAdapter(this, events, dates);
        listView.setAdapter(eventsAdapter);
        for (int i = 0; i < eventsAdapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
        Intent intent = new Intent();
        intent.putExtra(MESSAGE_KEY, eventsAdapter.getChild(i, i2).getTitle());
        setResult(RESULT_OK, intent);
        finish();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_events_from_calendar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                fillAdapter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                fillAdapter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}