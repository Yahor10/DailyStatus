package by.android.dailystatus.profile;

import static by.android.dailystatus.application.Constants.TAG;

import org.joda.time.DateTime;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import by.android.dailystatus.MainActivity;
import by.android.dailystatus.R;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

public class ProfileActivity extends SherlockActivity {
	
	private LayoutInflater inflater;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        
        inflater = getLayoutInflater();
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//setContent((TextView)findViewById(R.id.text));
    }

    /*protected void setContent(TextView view) {
        view.setText(R.string.action_items_content);
    }*/
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	onBackPressed();
        	/* If you need when call ProfileActivity not from MainActivity and onBack to MainActivity
        	 * 
        	 * Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
			   startActivity(intent);
			   finish();
			 */
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
