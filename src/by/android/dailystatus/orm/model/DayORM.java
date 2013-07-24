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
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "day")
public class DayORM {

	public static final String KEY_ID = "id";
	public static final String USER = "user";
	public static final String DAY = "day";
	public static final String MONTH = "month";
	public static final String YEAR = "year";
	public static final String PICTURE = "picture";
	public static final String STATUS = "status"; // -1 bad . 1 good

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
	@DatabaseField(columnName = PICTURE)
	public String pictureURL;
	@DatabaseField(columnName = STATUS)
	public int status;

	public DayORM() {

	}

	public DayORM(String user, int day, int month, int year) {
		super();
		this.user = user;
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public static void insertOrUpdateDay(Context context, DayORM day) {
		DatabaseHelper helper = OpenHelperManager.getHelper(context,
				DatabaseHelper.class);
		try {
			final Dao<DayORM, String> dao = helper.getDayDao();
			PreparedQuery<DayORM> prepare = dao.queryBuilder().where()
					.eq(DayORM.USER, day.user).and().eq(DayORM.DAY, day.day).and()
					.eq(DayORM.YEAR, day.year).prepare();
			List<DayORM> query = dao.query(prepare);
			if (query.isEmpty()) {
				if (dao.create(day) == 0) {
					throw new IllegalArgumentException("cannot create");
				}
			} else {
				UpdateBuilder<DayORM, String> updateBuilder = dao
						.updateBuilder();
				updateBuilder.updateColumnValue(DayORM.STATUS, day.status);
				updateBuilder.updateColumnValue(DayORM.PICTURE, day.pictureURL);
				PreparedUpdate<DayORM> prepare2 = updateBuilder.prepare();
				int update = dao.update(prepare2);
			}

		} catch (Exception e1) {
		} finally {
			OpenHelperManager.releaseHelper();
		}
	}

	public static void insertDays(Context context, List<DayORM> arrayList) {
		DatabaseHelper helper = OpenHelperManager.getHelper(context,
				DatabaseHelper.class);
		try {
			final Dao<DayORM, String> dao = helper.getDayDao();
			DeleteBuilder<DayORM, String> deleteBuilder = dao.deleteBuilder();
			int removed = dao.delete(deleteBuilder.prepare());
			for (DayORM app : arrayList) {
				dao.create(app);
			}
		} catch (Exception e1) {
		} finally {
			OpenHelperManager.releaseHelper();
		}
	}

	public static DayORM getDay(Context context, int day, int year) {
		DatabaseHelper helper = OpenHelperManager.getHelper(context,
				DatabaseHelper.class);
		List<DayORM> query = null;
		String currentUser = PreferenceUtils.getCurrentUser(context);
		try {
			final Dao<DayORM, String> dao = helper.getDayDao();
			PreparedQuery<DayORM> prepare = dao.queryBuilder().where()
					.eq(DayORM.USER, currentUser).and().eq(DayORM.DAY, day).and()
					.eq(DayORM.YEAR, year).prepare();
			query = dao.query(prepare);
		} catch (SQLException e) {
			return null;
		} finally {
			OpenHelperManager.releaseHelper();
		}
		if (query.isEmpty()) {
			return null;
		} else {
			return query.get(0);
		}
	}

	public static List<DayORM> getMonthDays(Context context, int month) {
		DatabaseHelper helper = OpenHelperManager.getHelper(context,
				DatabaseHelper.class);
		List<DayORM> query = null;
		try {
			final Dao<DayORM, String> dao = helper.getDayDao();
			PreparedQuery<DayORM> prepare = dao.queryBuilder().where()
					.eq(DayORM.MONTH, month).prepare();
			query = dao.query(prepare);
		} catch (SQLException e) {
			return null;
		} finally {
			OpenHelperManager.releaseHelper();
		}
		return query;
	}

	@Override
	public String toString() {
		return "DayORM [day=" + day + ", month=" + month + ", year=" + year
				+ ", pictureURL=" + pictureURL + ", color=" + status + "]";
	}
}
