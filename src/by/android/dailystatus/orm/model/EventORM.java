package by.android.dailystatus.orm.model;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import by.android.dailystatus.preference.PreferenceUtils;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "events")
public class EventORM {

	public static final String KEY_ID = "id";
	public static final String USER = "user";
	public static final String DAY = "day";
	public static final String MONTH = "month";
	public static final String YEAR = "year";
	public static final String TIME = "time";
	public static final String STATUS = "status"; // good ,bad
	public static final String DESCRIPTION = "description";

	@DatabaseField(columnName = KEY_ID)
	public int id;
	@DatabaseField(columnName = USER)
	public String user;
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

	public EventORM(String user, int day, int month, int year, long date,
			String description) {
		super();
		this.user = user;
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

	public static void insertEvent(Context context, EventORM event) {
		try {
			DatabaseHelper helper = OpenHelperManager.getHelper(context,
					DatabaseHelper.class);
			final Dao<EventORM, String> dao = helper.getEventDao();
			dao.create(event);
		} catch (Exception e1) {
		} finally {
			OpenHelperManager.releaseHelper();
		}
	}

	public static List<EventORM> getEvents(Context context) {
		List<EventORM> query = null;
		try {
			DatabaseHelper helper = OpenHelperManager.getHelper(context,
					DatabaseHelper.class);
			final Dao<EventORM, String> dao = helper.getEventDao();
			String currentUser = PreferenceUtils.getCurrentUser(context);
			PreparedQuery<EventORM> prepare = dao.queryBuilder().where()
					.eq(EventORM.USER, currentUser).prepare();
			query = dao.query(prepare);
		} catch (SQLException e) {
		} finally {
			OpenHelperManager.releaseHelper();
		}
		return query;
	}

	public static List<EventORM> getEventsByDay(Context context, int day,
			int year) {
		List<EventORM> query = null;
		try {
			DatabaseHelper helper = OpenHelperManager.getHelper(context,
					DatabaseHelper.class);
			final Dao<EventORM, String> dao = helper.getEventDao();
			String currentUser = PreferenceUtils.getCurrentUser(context);
			PreparedQuery<EventORM> prepare = dao.queryBuilder().where()
					.eq(EventORM.USER, currentUser).and().eq(EventORM.DAY, day)
					.and().eq(EventORM.YEAR, year).prepare();
			query = dao.query(prepare);
		} catch (SQLException e) {
		} finally {
			OpenHelperManager.releaseHelper();
		}
		return query;
	}

	public static boolean deleteEventById(Context context, String discr, int day) {
		try {
			DatabaseHelper helper = OpenHelperManager.getHelper(context,
					DatabaseHelper.class);
			final Dao<EventORM, String> dao = helper.getEventDao();

			DeleteBuilder<EventORM, String> deleteBuilder = dao.deleteBuilder();
			deleteBuilder.where().eq(DESCRIPTION, discr).and().eq(DAY, day);
			int key = deleteBuilder.delete();
			if (key < 0)
				return false;

		} catch (SQLException e) {
		} finally {
			OpenHelperManager.releaseHelper();
		}
		return true;
	}

	@Override
	public String toString() {
		return "EventORM [user=" + user + ", day=" + day + ", month=" + month
				+ ", year=" + year + ", description=" + description + "]";
	}
}
