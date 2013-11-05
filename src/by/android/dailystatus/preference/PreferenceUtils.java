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
		return defaultSharedPreferences.getString(PreferenceKeys.CURRENT_USER,
				"Johny");
	}

	public static void setImageFromCameraURL(Context context, String url) {
		Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		pEditor.putString(PreferenceKeys.CURRENT_IMAGE_FROM_CAMERA, url);
		pEditor.commit();
	}

	public static String getImageFromCameraURL(Context context) {
		SharedPreferences defaultSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return defaultSharedPreferences.getString(
				PreferenceKeys.CURRENT_IMAGE_FROM_CAMERA, "");
	}

	public static void setCurrentRadioNotification(Context context,
			int currentRad) {
		Editor pEditor = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		pEditor.putInt(PreferenceKeys.CURRENT_RADIO_BTN_NOTIFICATION,
				currentRad);
		pEditor.commit();
	}

	public static int getCurrentRadioNotification(Context context) {
		SharedPreferences defaultSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return defaultSharedPreferences.getInt(
				PreferenceKeys.CURRENT_RADIO_BTN_NOTIFICATION, 4);
	}

}
