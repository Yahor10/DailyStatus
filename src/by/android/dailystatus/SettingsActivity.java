package by.android.dailystatus;

import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import by.android.dailystatus.R;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends SherlockFragmentActivity  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
