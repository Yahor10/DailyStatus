package by.android.dailystatus.orm.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "events")
public class EventORM {

	public static final String KEY_ID = "id";
	public static final String DAY = "day";
	public static final String MONTH = "month";
	public static final String YEAR = "year";
	public static final String TIME = "time";
	public static final String STATUS = "status"; // good ,bad
	public static final String DESCRIPTION = "description";

	@DatabaseField(columnName = KEY_ID)
	public int id;
	@DatabaseField(columnName = DAY)
	public int day;
	@DatabaseField(columnName = MONTH)
	public int month;
	@DatabaseField(columnName = YEAR)
	public int year;
	@DatabaseField(columnName = TIME)
	public long date;
	@DatabaseField(columnName = STATUS)
	public int status;
	@DatabaseField(columnName = DESCRIPTION)
	public String description;

	public EventORM(int day, int month, int year, long date, String description) {
		super();
		this.day = day;
		this.month = month;
		this.year = year;
		this.date = date;
		this.description = description;
	}

	public EventORM() {

	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
