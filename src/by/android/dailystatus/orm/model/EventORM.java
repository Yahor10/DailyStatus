package by.android.dailystatus.orm.model;

import com.j256.ormlite.field.DatabaseField;

public class EventORM {

	public static final String KEY_ID = "id";
	public static final String DAY = "day";
	public static final String YEAR = "year";
	public static final String DATE = "date";
	public static final String DESCRIPTION = "description"; 

	@DatabaseField(columnName = KEY_ID)
	public int id;
	@DatabaseField(columnName = DAY)
	public int day;
	@DatabaseField(columnName = YEAR)
	public int year;
	@DatabaseField(columnName = DATE)
	public long date;
	@DatabaseField(columnName = DESCRIPTION)
	public String description;

	public EventORM() {

	}
}
