package by.android.dailystatus.social.twitter;

import by.android.dailystatus.SettingsActivity;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

public class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {

	final String TAG = getClass().getName();

	private SettingsActivity context;
	private OAuthProvider provider;
	private OAuthConsumer consumer;
	private Handler errorHandler;
	private String url;

	public OAuthRequestTokenTask(Context context, OAuthConsumer consumer,
			OAuthProvider provider, Handler errorHandler) {
		this.context = (SettingsActivity) context;
		this.consumer = consumer;
		this.provider = provider;
		this.errorHandler = errorHandler;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			Log.i(TAG, "Retrieving request token");

			url = provider.retrieveRequestToken(consumer,
					TwitterConstants.OAUTH_CALLBACK_URL);// запрос
			// на получение Access Token’ов
		} catch (Exception e) {
			Log.e(TAG, "Error during OAUth retrieve request token", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (!TextUtils.isEmpty(url)) {
			context.showTwitterDialog(url); // диалог авторизации
			// приложения. После того, как отработает – будет сгенерирован
			// интент, который вернет пользователя к нужному активити

		}
	}
}
