package by.android.dailystatus.orm.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "place")
public class DayORM {

	public static final String KEY_ID = "id";
	private static final String NAME = "name";
	private static final String MOOD = "mood";
	private static final String COLOR = "color";

	@DatabaseField(columnName = KEY_ID)
	public int id;
	@DatabaseField(columnName = NAME)
	public String name;
	@DatabaseField(columnName = MOOD)
	public int price;
	@DatabaseField(columnName = COLOR)
	public int rating;

	public DayORM() {

	}

}
