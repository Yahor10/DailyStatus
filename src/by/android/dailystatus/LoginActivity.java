package by.android.dailystatus;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import by.android.dailystatus.orm.model.UserORM;
import by.android.dailystatus.preference.PreferenceUtils;

public class LoginActivity extends Activity {

	// private Spinner mCountView;

	EditText loginEdit;
	EditText passwordEdit;

	private List<UserORM> allUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// startActivity(MainActivity.buildIntent(this));
		// finish();

		loginEdit = (EditText) findViewById(R.id.edtLogin);
		passwordEdit = (EditText) findViewById(R.id.edtPassword);

		findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String loginStr = loginEdit.getText().toString();
				String passwordStr = passwordEdit.getText().toString();

				if (loginStr.length() == 0) {

				} else {

					boolean containNameFlag = false;
					for (UserORM user : allUsers) {

						if (user.name.equals(loginStr)) {
							containNameFlag = true;

							if (user.password.equals(passwordStr)) {
								PreferenceUtils.setCurrentUser(
										getApplicationContext(), loginStr);
								startActivity(MainActivity
										.buildIntent(getApplicationContext()));
								finish();
							} else {
								Toast.makeText(LoginActivity.this,
										R.string.error_incorrect_password,
										Toast.LENGTH_SHORT).show();
							}
						}

					}
					if (!containNameFlag) {
						Toast.makeText(LoginActivity.this,
								R.string.error_name_does_not_exist,
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		Button btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
		btnCreateAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivityForResult(intent, 1);
				// finish();
			}
		});

	}
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data == null) {return;}
	    String loginStr = data.getStringExtra("login");
	    PreferenceUtils.setCurrentUser(
				getApplicationContext(), loginStr);
		startActivity(MainActivity
				.buildIntent(getApplicationContext()));
		finish();
	   
	  }

	@Override
	protected void onResume() {
		allUsers = UserORM.getAllFirstNames(this);
		super.onResume();
	}

}
