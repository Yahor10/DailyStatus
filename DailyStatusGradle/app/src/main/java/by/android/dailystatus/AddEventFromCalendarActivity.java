package by.android.dailystatus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AddEventFromCalendarActivity extends ActionBarActivity
        implements AdapterView.OnItemClickListener {

    public static final String MESSAGE_KEY = "message_key";

    private ArrayAdapter<String> eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_from_calendar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventsAdapter = new ArrayAdapter<String>(this,
                R.layout.item_event_list, R.id.txt_item_event, getEvents(this));
        ListView listView = (ListView) findViewById(R.id.events_list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(eventsAdapter);
    }

    private List<String> getEvents(Context context) {
        List<String> events = new ArrayList<String>();
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse("content://com.android.calendar/events"),
                        new String[]{"title", "dtstart"}, null, null, "dtstart desc");
        cursor.moveToFirst();

        do {
            events.add(cursor.getString(0));
        } while (cursor.moveToNext());

        return events;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        intent.putExtra(MESSAGE_KEY, eventsAdapter.getItem(i));
        setResult(RESULT_OK, intent);
        finish();
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