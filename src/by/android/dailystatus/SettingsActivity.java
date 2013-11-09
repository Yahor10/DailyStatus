package by.android.dailystatus;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import twitter4j.Twitter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import by.android.dailystatus.alarm.AlarmActivity;
import by.android.dailystatus.dialog.TwitterDialog;
import by.android.dailystatus.preference.PreferenceUtils;
import by.android.dailystatus.social.twitter.OAuthRequestTokenTask;
import by.android.dailystatus.social.twitter.RetrieveAccessTokenTask;
import by.android.dailystatus.social.twitter.TwitterConstants;
import by.android.dailystatus.social.twitter.TwitterUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;

public class SettingsActivity extends SherlockFragmentActivity implements
		OnClickListener {
	RadioGroup radioGroup;
	TextView versionTextView;

	public ProgressDialog progressDialog;
	public static Twitter twitter;
	public static OAuthConsumer consumer;
	public static OAuthProvider provider;

	TextView faceUserName;
	TextView facebookDescription;
	boolean faceLogin = false;

	public String twitterMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings);

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#0e78c9")));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		radioGroup = (RadioGroup) findViewById(R.id.rad_group);
		int index = PreferenceUtils
				.getCurrentRadioNotification(getApplicationContext());
		int id = R.id.radbtn_do_not_notify;
		switch (index) {
		case 0:
			id = R.id.radbtn_six_hours;
			break;
		case 2:
			id = R.id.radbtn_three_hours;
			break;

		default:
			break;
		}
		radioGroup.check(id);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				View radioButton = radioGroup.findViewById(checkedId);
				int idx = radioGroup.indexOfChild(radioButton);

				PreferenceUtils.setCurrentRadioNotification(
						getApplicationContext(), idx);

				switch (idx) {
				case 0:

				case 2:
					AlarmActivity.setRepeatingAlarm(getApplicationContext(),
							idx);

					break;
				case 4:
					AlarmActivity.CancelAlarm(getApplicationContext());

					break;

				default:
					break;
				}

			}
		});

		PackageInfo pInfo;
		String vers = "";
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			vers = pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		versionTextView = (TextView) findViewById(R.id.txt_version_description);
		versionTextView.setText("v" + vers);
		faceUserName = (TextView) findViewById(R.id.txt_face_user_name);
		facebookDescription = (TextView) findViewById(R.id.txt_facebook_descroption);

		findViewById(R.id.lay_rate).setOnClickListener(this);
		findViewById(R.id.lay_connect_with_developer).setOnClickListener(this);
		findViewById(R.id.lay_twitter).setOnClickListener(this);
		findViewById(R.id.lay_facebook).setOnClickListener(this);

		Session.openActiveSession(this, false, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {

					Request.newMeRequest(session,
							new Request.GraphUserCallback() {

								@Override
								public void onCompleted(GraphUser user,
										Response response) {

									if (user != null) {
										faceUserName.setText(user
												.getFirstName()
												+ " "
												+ user.getLastName());
										facebookDescription
												.setText("Нажмите чтобы выйти");
										faceLogin = true;
									}
								}
							}).executeAsync();
				}

			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.lay_rate:
			// new AppRate(this).setShowIfAppHasCrashed(false).init();

			break;

		case R.id.lay_facebook:
			// start Facebook Login
			Session.openActiveSession(this, true, new Session.StatusCallback() {

				// callback when session changes state
				@Override
				public void call(Session session, SessionState state,
						Exception exception) {

					if (session.isOpened()) {

						try {
							Bundle params = new Bundle();
							params.putString("name", "Facebook SDK for Android");// title
							params.putString("caption",
									"Build great social apps and get more installs.");//
							params.putString(
									"description",
									"The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");

							WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
									SettingsActivity.this, Session
											.getActiveSession(), params))
									.setOnCompleteListener(null).build();
							feedDialog.show();
						} catch (Exception e) {
							e.printStackTrace();
						}
						// if (!faceLogin) {
						// Request.newMeRequest(session,
						// new Request.GraphUserCallback() {
						//
						// @Override
						// public void onCompleted(GraphUser user,
						// Response response) {
						//
						// if (user != null) {
						// faceUserName.setText(user
						// .getFirstName()
						// + " "
						// + user.getLastName());
						// facebookDescription
						// .setText("Нажмите чтобы выйти");
						// }
						// }
						// }).executeAsync();
						// } else {
						// session.closeAndClearTokenInformation();
						//
						// faceUserName.setText(getResources().getString(
						// R.string.facebook));
						// facebookDescription.setText("Нажмите чтобы войти");
						//
						// }
					}

				}
			});

			break;

		case R.id.lay_connect_with_developer:

			String recepientEmail = "sekt88@gmail.com"; // either set to
														// destination
			// email or leave empty
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:" + recepientEmail));

			try {
				startActivity(Intent.createChooser(intent, "Send Email"));
			} catch (Exception e) {
				// TODO: handle exception
			}

			break;

		case R.id.lay_twitter:

			if (isOnline()) {
				if (twitter == null) {
					signOnTwitter();

				}
			} else {
				Toast.makeText(this, "РќР•РўРЈ Р�РќРўР•Р Р•РќР•РўРђ",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}

	}

	public void signOnTwitter() {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (twitter == null) {
			twitter = TwitterUtils.isAuthenticated(prefs);
		}

		if (twitter == null) {
			progressDialog = ProgressDialog.show(this, "", "Please wait");
			twitterMessage = null;
			Toast.makeText(this, "Please authorize this app!",
					Toast.LENGTH_LONG).show();
			consumer = new CommonsHttpOAuthConsumer(
					TwitterConstants.CONSUMER_KEY,
					TwitterConstants.CONSUMER_SECRET);
			provider = new CommonsHttpOAuthProvider(
					TwitterConstants.REQUEST_URL, TwitterConstants.ACCESS_URL,
					TwitterConstants.AUTHORIZE_URL);
			new OAuthRequestTokenTask(this, consumer, provider, new Handler() {

				@Override
				public void handleMessage(Message msg) {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					Toast.makeText(
							SettingsActivity.this,
							"Error during Twitter Authorization: "
									+ "OAUth retrieve request token failed",
							Toast.LENGTH_LONG).show();
				}

			}).execute();
		}
	}

	public void showTwitterDialog(String url) {
		new TwitterDialog(this, url).show();
	}

	public boolean wasOffline;

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			wasOffline = false;
			return true;
		}
		return false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (intent != null && intent.getData() != null) {
			Uri uri = intent.getData();
			if (uri != null
					&& uri.toString().startsWith(
							TwitterConstants.OAUTH_CALLBACK_URL)) {
				new RetrieveAccessTokenTask(this, consumer, provider,
						twitterMessage, new Handler() {

							@Override
							public void handleMessage(Message msg) {
								if (msg != null && msg.obj != null) {
									if (msg.arg1 == RetrieveAccessTokenTask.RETRIEVAL_FAIL) {
										// Log.d(TAG, (String) msg.obj);
										Toast.makeText(
												SettingsActivity.this,
												"Failed to retrieve access token",
												Toast.LENGTH_LONG).show();
									} else if (msg.arg1 == RetrieveAccessTokenTask.RETRIEVAL_SUCCESS) {
										Toast.makeText(SettingsActivity.this,
												"С‡С‘-С‚Рѕ РЅРµ С‚Р°Рє",
												Toast.LENGTH_SHORT).show();
									}
								}

								if (progressDialog != null) {
									progressDialog.dismiss();
									progressDialog = null;
								}
							}

						}).execute(uri);
			}
		} else {
			// HIDE progress dialog (can be visible after twitter authorization
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

}
