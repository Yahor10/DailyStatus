package by.android.dailystatus.fragment;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;

import by.android.dailystatus.widget.container.EventLayout;

import android.provider.CalendarContract.EventDays;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DayModel {

	private int index;
	private String text;
	public TextView dayText;
	public ImageView dayImage;
	public Button goodDay;
	public Button badDay;
	public EventLayout eventLayout;
	public DateTime date;
	

	public DayModel(int index) {
		this.index = index;
		setIndex(index);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		setDayText(index);
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
		
		DateTime.Property pDoW = now.dayOfWeek();
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
}