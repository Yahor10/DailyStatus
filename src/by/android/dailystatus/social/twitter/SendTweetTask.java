package by.android.dailystatus.social.twitter;

import by.android.dailystatus.SettingsActivity;
import twitter4j.TwitterException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class SendTweetTask extends AsyncTask<Void, Void, Void> {

	private String message;
	private Handler handler;

	public SendTweetTask(String message, Handler handler) {
		this.message = message;
		this.handler = handler;
	}

	@Override
	protected Void doInBackground(Void... params) {
		String result;
		// опять же пользуемся статическим полем twitter из Вашего активити
		if (SettingsActivity.twitter != null) {
			try {

				SettingsActivity.twitter.updateStatus(message);
				result = "Your message has been sent";
			} catch (TwitterException e) {
				result = "Error sending message";
			}
			if (handler != null) {
				Message msg = new Message();
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}

		return null;
	}
}
