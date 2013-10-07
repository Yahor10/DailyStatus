package by.android.dailystatus.orm.model;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

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
	public static final String SEX = "sex";
	public static final String PASSWORD = "password";
	public static final String EMAIL = "email";


	@DatabaseField(generatedId = true, columnName = KEY_ID)
	public int id;
	@DatabaseField(columnName = NAME)
	public String name;
	@DatabaseField(columnName = LAST_NAME)
	public String lastName;
	@DatabaseField(columnName = PASSWORD)
	public String password;
	@DatabaseField(columnName = EMAIL)
	public String email;
	@DatabaseField(columnName = SEX)
	public int sex;

	public UserORM() {

	}

	public UserORM(String name, String lastName,int sex, String password, String email) {
		super();
		this.name = name;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.sex = sex;
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

	public static List<UserORM> getAllFirstNames(Context context) {
		DatabaseHelper helper = OpenHelperManager.getHelper(context,
				DatabaseHelper.class);
		Dao<UserORM, String> dao = null;
		List<UserORM> query = null;
		try {
			dao = helper.getUserDao();
			PreparedQuery<UserORM> prepare = dao.queryBuilder().prepare();
			query = dao.query(prepare);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return query;
	}

}
