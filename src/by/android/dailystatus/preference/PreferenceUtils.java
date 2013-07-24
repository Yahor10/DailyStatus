package by.android.dailystatus.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtils {

	public static void setCurrentUser(Context context, String masterPassword) {
		Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		pEditor.putString(PreferenceKeys.CURRENT_USER, masterPassword);
		pEditor.commit();
	}

	public static String getCurrentUser(Context context) {
		SharedPreferences defaultSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return defaultSharedPreferences.getString(
				PreferenceKeys.CURRENT_USER, "Johny");
	}

}
