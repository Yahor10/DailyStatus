/**
 *
 */
package by.android.dailystatus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewEventActivity extends ActionBarActivity {

    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_DESCRIPTION = "extra_description";

    private SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");

    public static Intent buildIntent(Context packageContext, long date,
                                     String description) {
        Intent intent = new Intent(packageContext, ViewEventActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_DESCRIPTION, description);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#ffffffff")));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView eventDescription = (TextView) findViewById(R.id.eventDescription);

        Intent intent = getIntent();

        if (intent != null) {
            long dateExtra = intent.getLongExtra(EXTRA_DATE, 0);
            Date date = new Date(dateExtra);
            String dateStr = formatter.format(date);
            setTitle(dateStr);

            String descriptionExtra = intent.getStringExtra(EXTRA_DESCRIPTION);
            eventDescription.setText(descriptionExtra);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
