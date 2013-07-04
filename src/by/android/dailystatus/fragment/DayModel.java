package by.android.dailystatus.fragment;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;

import android.widget.ImageView;
import android.widget.TextView;

public class DayModel {

	private int index;
	private String text;
	public TextView dayText;
	public ImageView dayImage;
	

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
		DateTime.Property pDoW = now.dayOfWeek();
		String day = pDoW.getAsText();
		Property pMoY = now.monthOfYear();
		String month = pMoY.getAsText();
		int dayOfMonth = now.getDayOfMonth();
		
		StringBuffer dataBuilder = new StringBuffer();
		dataBuilder.append(day);
		dataBuilder.append(" ");
		dataBuilder.append(dayOfMonth);
		dataBuilder.append(". ");
		dataBuilder.append(month);
		
		this.text = dataBuilder.toString();
	}
}