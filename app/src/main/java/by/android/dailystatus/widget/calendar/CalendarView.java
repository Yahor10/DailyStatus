package by.android.dailystatus.widget.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import by.android.dailystatus.ChartsActivity;
import by.android.dailystatus.R;
import by.android.dailystatus.application.Constants;
import by.android.dailystatus.orm.model.DayORM;
import by.android.dailystatus.preference.PreferenceUtils;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressLint("NewApi")
public class CalendarView extends ActionBarActivity implements
        OnMenuItemClickListener, OnClickListener {
    private String KEY_WAS_SHOWED_GOOD_DAYS = "key_good";
    private String KEY_WAS_SHOWED_BAD_DAYS = "key_bad";
    private String KEY_SELECT_DATE = "key_select_date";

    public GregorianCalendar month, itemmonth;// calendar instances.

    public CalendarAdapter adapter;// adapter instance
    public Handler handler;// for grabbing some event values for showing the dot
    // marker.
    public ArrayList<String> items; // container to store calendar items which
    // needs showing the event marker

    private String selectedDate;

    Button butGood;
    Button butBad;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        if (savedInstanceState != null) {
            boolean abv = savedInstanceState.getBoolean("MyBoolean");
        }

        setTitle(R.string.calendar);
        Locale.setDefault(Locale.US);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO find correct place for show events button
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        itemmonth = (GregorianCalendar) month.clone();

        items = new ArrayList<String>();
        adapter = new CalendarAdapter(this, month);

        selectedDate = adapter.getCurrentDateString();

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        handler = new Handler();
        handler.post(calendarUpdater);

        butBad = (Button) findViewById(R.id.but_bad);
        butGood = (Button) findViewById(R.id.but_good);

        butBad.setOnClickListener(this);
        butGood.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        findViewById(R.id.previous).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });

        findViewById(R.id.next).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();

            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                String selectedGridDate = CalendarAdapter.dayString
                        .get(position);
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*",
                        "");// taking last part of date. ie; 2 from 2012-12-02.
                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                selectedDate = selectedGridDate;
                showToast(selectedGridDate);
            }
        });
    }

    boolean flag_showed_good_day = false;
    boolean flag_showed_bad_day = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_WAS_SHOWED_BAD_DAYS, flag_showed_bad_day);
        outState.putBoolean(KEY_WAS_SHOWED_GOOD_DAYS, flag_showed_good_day);
        outState.putString(KEY_SELECT_DATE, selectedDate);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        flag_showed_bad_day = savedInstanceState
                .getBoolean(KEY_WAS_SHOWED_BAD_DAYS);
        flag_showed_good_day = savedInstanceState
                .getBoolean(KEY_WAS_SHOWED_GOOD_DAYS);
        selectedDate = savedInstanceState.getString(KEY_SELECT_DATE);

        if (flag_showed_bad_day) {
            adapter.setBadDays(getBadDays());
            adapter.setCurentDateString(selectedDate);
            adapter.notifyDataSetChanged();
        } else if (flag_showed_good_day) {
            adapter.setGoodDays(getGoodDays());
            adapter.setCurentDateString(selectedDate);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMyProfile = menu.addSubMenu("").setIcon(
                getResources().getDrawable(R.drawable.ic_override_menu));

        subMyProfile.add(0, 4, Menu.NONE, "Show Good days")
                .setIcon(R.drawable.ic_good_day)
                .setOnMenuItemClickListener(this);
        subMyProfile.add(0, 5, Menu.NONE, "Show Bad days")
                .setIcon(R.drawable.ic_bad_day)
                .setOnMenuItemClickListener(this);
        subMyProfile.add(0, 6, Menu.NONE, "Show month chart")
                .setIcon(R.drawable.ic_menu_graphics).setOnMenuItemClickListener(this);
        subMyProfile.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public Set<Integer> getGoodDays() {
        int month1, month3;
        int year1, year3;
        int monthNumber = month.get(Calendar.MONTH) + 1;
        int yearNumber = month.get(Calendar.YEAR);
        month1 = monthNumber - 1;
        month3 = monthNumber + 1;
        year1 = year3 = yearNumber;
        if (month1 <= 0) {
            year1--;
            month1 = 12;
        }

        if (month1 > 12) {
            year1++;
            month1 = 1;
        }

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d1 = null;
        Calendar tdy1;
        // /bla bla

        try {
            d1 = form.parse(selectedDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        tdy1 = Calendar.getInstance();
        tdy1.setTime(d1);

        List<DayORM> goodDaysByMonth = DayORM.getGoodDaysByMonth(this,
                monthNumber, yearNumber);
        List<DayORM> goodDaysByLastMonth = DayORM.getGoodDaysByMonth(this,
                month1, year1);
        List<DayORM> goodDaysByNextMonth = DayORM.getGoodDaysByMonth(this,
                month3, year3);
        Set<Integer> goodDays = new HashSet<Integer>();
        for (DayORM dayORM : goodDaysByMonth) {
            goodDays.add(dayORM.day);
        }
        for (DayORM dayORM : goodDaysByLastMonth) {
            goodDays.add(dayORM.day);
        }
        for (DayORM dayORM : goodDaysByNextMonth) {
            goodDays.add(dayORM.day);
        }
        return goodDays;
    }

    public Set<Integer> getBadDays() {
        int month1, month3;
        int year1, year3;
        int monthNumber = month.get(Calendar.MONTH) + 1;
        int yearNumber = month.get(Calendar.YEAR);
        month1 = monthNumber - 1;
        month3 = monthNumber + 1;
        year1 = year3 = yearNumber;
        if (month1 <= 0) {
            year1--;
            month1 = 12;
        }

        if (month1 > 12) {
            year1++;
            month1 = 1;
        }

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d1 = null;
        Calendar tdy1;
        // /bla bla

        try {
            d1 = form.parse(selectedDate);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tdy1 = Calendar.getInstance();
        tdy1.setTime(d1);

        List<DayORM> badDaysByMonth = DayORM.getBadDaysByMonth(this,
                monthNumber, yearNumber);
        List<DayORM> badDaysByLastMonth = DayORM.getBadDaysByMonth(this,
                month1, year1);
        List<DayORM> badDaysByNextMonth = DayORM.getBadDaysByMonth(this,
                month3, year3);
        Set<Integer> badDays = new HashSet<Integer>();
        for (DayORM dayORM : badDaysByMonth) {
            badDays.add(dayORM.day);
        }
        for (DayORM dayORM : badDaysByLastMonth) {
            badDays.add(dayORM.day);
        }
        for (DayORM dayORM : badDaysByNextMonth) {
            badDays.add(dayORM.day);
        }
        return badDays;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();

        int month1, month3;
        int year1, year3;
        int monthNumber = month.get(Calendar.MONTH) + 1;
        int yearNumber = month.get(Calendar.YEAR);
        month1 = monthNumber - 1;
        month3 = monthNumber + 1;
        year1 = year3 = yearNumber;
        if (month1 <= 0) {
            year1--;
            month1 = 12;
        }

        if (month1 > 12) {
            year1++;
            month1 = 1;
        }

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d1 = null;
        Calendar tdy1;
        // /bla bla

        try {
            d1 = form.parse(selectedDate);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tdy1 = Calendar.getInstance();
        tdy1.setTime(d1);

        switch (itemId) {
            case 4:
                flag_showed_bad_day = false;
                flag_showed_good_day = true;

                adapter.setGoodDays(getGoodDays());
                adapter.setCurentDateString(selectedDate);
                adapter.notifyDataSetChanged();
                break;
            case 5:
                flag_showed_bad_day = true;
                flag_showed_good_day = false;

                adapter.setBadDays(getBadDays());
                adapter.setCurentDateString(selectedDate);
                adapter.notifyDataSetChanged();
                break;
            case 6:
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date parse = new Date();
                try {
                    parse = format.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                LocalDate date = new LocalDate(parse);
                Log.i(Constants.TAG, "DATE" + date);
                int dayOfYear = date.getDayOfYear();
                int monthOfYear = date.getMonthOfYear();
                int year = date.getYear();
                Intent buintIntent = ChartsActivity.buintIntent(this, dayOfYear,
                        monthOfYear, year, true);
                startActivity(buintIntent);
                // TODO check activity stack
                break;
            default:
                break;
        }
        return true;
    }

    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }

    }

    protected void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();

    }

    public void refreshCalendar() {
        TextView title = (TextView) findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some calendar items

        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            items.clear();

            // Print dates of the current week
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String itemvalue;
            for (int i = 0; i < 7; i++) {
                itemvalue = df.format(itemmonth.getTime());
                itemmonth.add(GregorianCalendar.DATE, 1);
                items.add("2012-09-12");
                items.add("2012-10-07");
                items.add("2012-10-15");
                items.add("2012-10-20");
                items.add("2012-11-30");
                items.add("2012-11-28");
            }

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };

    public static Intent buintIntent(Context context) {
        return new Intent(context, CalendarView.class);
    }

    @Override
    public void onClick(View v) {

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d1 = null;
        Calendar tdy1;
        // /bla bla

        try {
            d1 = form.parse(selectedDate);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tdy1 = Calendar.getInstance();
        tdy1.setTime(d1);

        int day = tdy1.get(Calendar.DAY_OF_YEAR);
        int monthNumber = tdy1.get(Calendar.MONTH);
        int yearNumber = tdy1.get(Calendar.YEAR);
        String currentUser = PreferenceUtils.getCurrentUser(this);
        DayORM dayORM = new DayORM(currentUser, day, monthNumber, yearNumber);
        switch (v.getId()) {
            case R.id.but_bad:

                dayORM.status = -1;

                DayORM.insertOrUpdateDay(this, dayORM);

                adapter.addBadDays(dayORM.day);
                adapter.setCurentDateString(selectedDate);
                adapter.notifyDataSetChanged();

                break;
            case R.id.but_good:

                dayORM.status = 1;

                DayORM.insertOrUpdateDay(getApplicationContext(), dayORM);

                adapter.addGoodDays(dayORM.day);
                adapter.setCurentDateString(selectedDate);
                adapter.notifyDataSetChanged();

                break;

            default:
                break;
        }

    }

}
