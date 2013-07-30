package by.android.dailystatus.orm.model;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class UserORM {

	public static final String KEY_ID = "id";
	public static final String NAME = "name";
	public static final String LAST_NAME = "last_name";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";

	@DatabaseField(columnName = KEY_ID)
	public int id;
	@DatabaseField(columnName = NAME)
	public String name;
	@DatabaseField(columnName = LAST_NAME)
	public String lastName;
	@DatabaseField(columnName = PASSWORD)
	public String password;
	@DatabaseField(columnName = EMAIL)
	public String email;

	public UserORM() {

	}

	public UserORM(int id, String name, String lastName, String password,
			String email) {
		super();
		this.id = id;
		this.name = name;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
	}

	public static void insertUser(Context context, UserORM user) {
		DatabaseHelper helper = OpenHelperManager.getHelper(context,
				DatabaseHelper.class);
		try {
			final Dao<UserORM, String> dao = helper.getUserDao();
			if (dao.create(user) == 0) {
				throw new IllegalArgumentException("cannot create");
			}
		} catch (Exception e1) {
		} finally {
			OpenHelperManager.releaseHelper();
		}
	}

}
