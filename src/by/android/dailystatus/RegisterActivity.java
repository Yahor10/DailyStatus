package by.android.dailystatus;

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

public class RegisterActivity extends Activity  {

	private String[] data = {"woman", "man"};
	private String sex;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etEmail;
	private EditText etPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		etFirstName = (EditText) findViewById(R.id.etFirstName);
		etLastName = (EditText) findViewById(R.id.etFirstName);
		etEmail = (EditText) findViewById(R.id.etFirstName);
		etPassword = (EditText) findViewById(R.id.etFirstName);
		Spinner spinnerSex = (Spinner) findViewById(R.id.spinner);
		Button btnOk = (Button) findViewById(R.id.btnOk);
		
		spinnerSex.setAdapter(adapter);
		spinnerSex.setPrompt(getResources().getString(R.string.sex));
		spinnerSex.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				sex = data[position];
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
				PreferenceUtils.setCurrentUser(getApplicationContext(), firstName);
				//не знаю что присваивать полю id
				//насколько я понимаю мы id должны автоматом генерить 
				//в зависимости от того, какой последний у нас в базе, 
				//т.е. id = (n+1), где n = последний id в БД
				//но я не знаю где брать это значение
				//может чего тут неправильно я делаю
				int id = 0;
				UserORM.insertUser(RegisterActivity.this, new UserORM(firstName, lastName, password, email));
				UserORM.getAllFirstNames(RegisterActivity.this, firstName);
			}
		});
		
		
		
	}

}

