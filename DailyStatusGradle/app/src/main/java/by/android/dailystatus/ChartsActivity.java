package by.android.dailystatus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import by.android.dailystatus.fragment.MonthFragment;
import by.android.dailystatus.fragment.WeekFragment;
import by.android.dailystatus.fragment.YearFragment;
import com.viewpagerindicator.TabPageIndicator;

public class ChartsActivity extends ActionBarActivity {

    public static final String WEEK = "Week_day";
    public static final String MONTH = "Month";
    public static final String YEAR = "Year";
    private static final String FROM_CALENDAR = "from_calendar";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.simple_tabs);

        //change default overlays
        ((ImageView) findViewById(R.id.overlay_left)).setImageResource(R.drawable.ic_grape_green_calendar);
        ((ImageView) findViewById(R.id.overlay_right)).setImageResource(R.drawable.pear_mirrored);

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#ffffffff")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentPagerAdapter adapter = new AppsAdapter(
                getSupportFragmentManager());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);


        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        boolean fromCalendar = getIntent()
                .getBooleanExtra(FROM_CALENDAR, false);

        if (fromCalendar) {
            pager.setCurrentItem(1);
        } else {
            pager.setCurrentItem(0);
        }
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

    private class AppsAdapter extends FragmentPagerAdapter {

        public AppsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return WeekFragment.newInstance();
                case 1:
                    return MonthFragment.newInstance();
                case 2:
                    return YearFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.week);
                case 1:
                    return getString(R.string.month);
                case 2:
                    return getString(R.string.year);
                default:
                    return "";
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static Intent buintIntent(Context context, int week, int month,
                                     int year) {
        Intent intent = new Intent(context, ChartsActivity.class);
        intent.putExtra(WEEK, week);
        intent.putExtra(MONTH, month);
        intent.putExtra(YEAR, year);
        return intent;
    }

    public static Intent buintIntent(Context context, int week, int month,
                                     int year, boolean fromCalendar) {
        Intent intent = new Intent(context, ChartsActivity.class);
        intent.putExtra(WEEK, week);
        intent.putExtra(MONTH, month);
        intent.putExtra(YEAR, year);
        intent.putExtra(FROM_CALENDAR, fromCalendar);
        return intent;
    }
}
