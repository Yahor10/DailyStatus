package by.android.dailystatus;

import java.util.List;

import by.android.dailystatus.orm.model.UserORM;
import by.android.dailystatus.preference.PreferenceUtils;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private int sex;
	private String firstName;
	private String lastName;
	private String email;
	private String password;

	private EditText etFirstName;
	private EditText etLastName;
	private EditText etEmail;
	private EditText etPassword;
	private List<UserORM> allFirstNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		String[] data = getResources().getStringArray(R.array.sex);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		etFirstName = (EditText) findViewById(R.id.etFirstName);
		etFirstName.setError(getResources().getString(R.string.please_enter_first_name));
		etLastName = (EditText) findViewById(R.id.etFirstName);
		etEmail = (EditText) findViewById(R.id.etFirstName);
		etPassword = (EditText) findViewById(R.id.etFirstName);
		Spinner spinnerSex = (Spinner) findViewById(R.id.spinner);
		Button btnOk = (Button) findViewById(R.id.btnOk);

		spinnerSex.setAdapter(adapter);
		spinnerSex.setPrompt(getResources().getString(R.string.sex));
		spinnerSex.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				sex = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				firstName = etFirstName.getText().toString();
				lastName = etLastName.getText().toString();
				email = etEmail.getText().toString();
				password = etPassword.getText().toString();
				Context applicationContext = getApplicationContext();
				for (UserORM userORM : allFirstNames) {
					if (userORM.name.equals(firstName)) {
						Toast.makeText(applicationContext, "NAME EXIST",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				UserORM.insertUser(RegisterActivity.this, new UserORM(
						firstName, lastName, sex, password, email));
				PreferenceUtils.setCurrentUser(applicationContext, firstName);
				startActivity(MainActivity.buildIntent(applicationContext));
				finish();
				// send broadcast to close login activity
			}
		});

	}

	@Override
	protected void onResume() {
		allFirstNames = UserORM
				.getAllFirstNames(RegisterActivity.this);
		super.onResume();
	}

}
