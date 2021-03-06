package by.android.dailystatus.widget.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import by.android.dailystatus.R;
import by.android.dailystatus.application.Constants;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarAdapter extends BaseAdapter {
    private Context mContext;

    private java.util.Calendar month;
    public GregorianCalendar pmonth; // calendar instance for previous month
    /**
     * calendar instance for previous month for getting complete view
     */
    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    int calMaxP;
    int lastWeekDay;
    int leftDays;
    int mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;

    private ArrayList<String> items;
    public static List<String> dayString;
    private View previousView;

    private int firstMonthDay;
    private int lastMonthDay;

    private Set<Integer> goodDays = new HashSet<Integer>();
    private Set<Integer> badDays = new HashSet<Integer>();

    public CalendarAdapter(Context c, GregorianCalendar monthCalendar) {
        CalendarAdapter.dayString = new ArrayList<String>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        mContext = c;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<String>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();
    }

    public String getCurrentDateString() {
        return curentDateString;
    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() {
        int size = dayString.size();
        return size;
    }

    public Object getItem(int position) {
        return dayString.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);

        }
        dayView = (TextView) v.findViewById(R.id.date);
        // separates daystring into parts.
        String[] separatedTime = dayString.get(position).split("-");
        // taking last part of date. ie; 2 from 2012-12-02
        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        // checking whether the day is in current month or not.
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            // setting offdays to white color.
            dayView.setTextColor(mContext.getResources().getColor(R.color.lighter_gray));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(mContext.getResources().getColor(R.color.lighter_gray));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // setting curent month's days in blue color.
            dayView.setTextColor(Color.WHITE);
        }

        dayView.setText(gridvalue);

        if (dayString.get(position).equals(curentDateString)) {
            setSelected(v);
            previousView = v;
        } else {
            v.setBackgroundColor(mContext.getResources().getColor(R.color.light_gray));
        }

        // create date string for comparison
        String date = dayString.get(position);

        Date date2;
        try {
            date2 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            LocalDate local = new LocalDate(date2);
            int dayOfYear = local.getDayOfYear();

            v.setTag(dayOfYear);

            if (goodDays.contains(dayOfYear)) {
                v.setBackgroundColor(mContext.getResources().getColor(R.color.red_border));
            }

            if (badDays.contains(dayOfYear)) {
                v.setBackgroundColor(mContext.getResources().getColor(R.color.limon_color));
                dayView.setTextColor(mContext.getResources().getColor(R.color.red_border));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

//        show icon if date is not empty and it exists in the items array
//        ImageView iw = (ImageView) v.findViewById(R.id.date_icon);
//        if (date.length() > 0 && items != null && items.contains(date)) {
//            iw.setVisibility(View.VISIBLE);
//        } else {
//            iw.setVisibility(View.INVISIBLE);
//        }
        return v;
    }

    public View setSelected(View view) {
        if (previousView != null) {
            if (previousView.getTag() != null) {
                int dayOfYear = (Integer) previousView.getTag();

                if (goodDays.contains(dayOfYear)) {
                    previousView.setBackgroundColor(mContext.getResources().getColor(R.color.red_border));
                } else if (badDays.contains(dayOfYear)) {
                    previousView.setBackgroundColor(mContext.getResources().getColor(R.color.limon_color));
                    ((TextView) previousView.findViewById(R.id.date))
                            .setTextColor(mContext.getResources().getColor(R.color.red_border));
                } else {
                    previousView.setBackgroundColor(mContext.getResources().getColor(R.color.light_gray));
                }
            }
        }
        previousView = view;
        view.setBackgroundColor(mContext.getResources().getColor(android.R.color.darker_gray));
        return view;
    }

    public void refreshDays() {
        // clear items
        items.clear();
        dayString.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...

        Calendar cal = (Calendar) month.clone();
        cal.set(Calendar.DAY_OF_MONTH,
                Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        firstMonthDay = cal.get(Calendar.DAY_OF_YEAR);
        cal.set(Calendar.DAY_OF_MONTH,
                Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        lastMonthDay = cal.get(Calendar.DAY_OF_YEAR);

        Log.v(Constants.TAG, "FIRST" + firstMonthDay);
        Log.v(Constants.TAG, "LAST" + lastMonthDay);
        /**
         * Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.
         */
        pmonthmaxset = (GregorianCalendar) pmonth.clone();
        /**
         * setting the start date as previous month's required date.
         */
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        /**
         * filling calendar gridview.
         */
        for (int n = 0; n < mnthlength; n++) {
            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            dayString.add(itemvalue);
        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }

    public void setGoodDays(Set<Integer> goodDays) {
        this.badDays.clear();
        this.goodDays = goodDays;
    }

    public void addGoodDays(int goodDays) {
        this.goodDays.add(goodDays);
        if (this.badDays.contains(goodDays)) {
            this.badDays.remove(goodDays);
        }
    }

    public void setBadDays(Set<Integer> badDays) {
        this.goodDays.clear();
        this.badDays = badDays;
    }

    public void addBadDays(int badDays) {
        this.badDays.add(badDays);
        if (this.goodDays.contains(badDays)) {
            this.goodDays.remove(badDays);
        }

    }

    public void setCurentDateString(String curentDateString) {
        this.curentDateString = curentDateString;
    }

}