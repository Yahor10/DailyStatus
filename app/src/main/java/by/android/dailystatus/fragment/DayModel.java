package by.android.dailystatus.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;

import by.android.dailystatus.widget.container.EventLayout;

public class DayModel {

    private int index;
    private String text;
    public TextView dayText;
    public ImageView dayImage;
    public View goodDay;
    public View badDay;
    public EventLayout eventLayout;
    public DateTime date;

    public DayModel(int index, Context context) {
        this.index = index;
        setIndex(index);
    }

    public DayModel(int index, Context applicationContext, DateTime now) {
        this.index = index;
        setIndex(index, now);
    }

    public void setIndex(int index) {
        this.index = index;
        setDayText(index);
    }

    public void setIndex(int index, DateTime now) {
        this.index = index;
        setDayText(index, now);
    }

    public int getIndex() {
        return index;
    }

    public String getDayText() {
        return text;
    }

    private void setDayText(int index) {
        DateTime now = DateTime.now();
        if (index > 0) {
            now = now.plusDays(Math.abs(index));
        } else {
            now = now.minusDays(Math.abs(index));
        }
        setDate(now);

        Property pMoY = now.monthOfYear();
        String month = pMoY.getAsText();
        int dayOfMonth = now.getDayOfMonth();

        StringBuffer dataBuilder = new StringBuffer();
        dataBuilder.append(dayOfMonth);
        dataBuilder.append(". ");
        dataBuilder.append(month);

        this.text = dataBuilder.toString();
    }

    private void setDayText(int index, DateTime now) {
        setDate(now);

        Property pMoY = now.monthOfYear();
        String month = pMoY.getAsText();
        int dayOfMonth = now.getDayOfMonth();

        StringBuffer dataBuilder = new StringBuffer();
        dataBuilder.append(dayOfMonth);
        dataBuilder.append(". ");
        dataBuilder.append(month);
        this.text = dataBuilder.toString();
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public void setDayText(DateTime now) {
        setDate(now);

        // DateTime.Property pDoW = now.dayOfWeek();
        Property pMoY = now.monthOfYear();
        String month = pMoY.getAsText();
        int dayOfMonth = now.getDayOfMonth();

        StringBuffer dataBuilder = new StringBuffer();
        dataBuilder.append(dayOfMonth);
        dataBuilder.append(". ");
        dataBuilder.append(month);

        this.text = dataBuilder.toString();
    }
}