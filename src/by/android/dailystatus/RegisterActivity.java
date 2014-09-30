package by.android.dailystatus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import by.android.dailystatus.orm.model.UserORM;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity {

    private final static int MIN_COUNT_PASSWORD_SIZE = 5;

    private int sex;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;

    private EmailValidator emailValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        emailValidator = new EmailValidator();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        String[] data = getResources().getStringArray(R.array.sex);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        etFirstName = (EditText) findViewById(R.id.etFirstName);

        etFirstName.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && etFirstName.getText().toString().length() < 1) {
                    etFirstName.setError(getResources().getString(
                            R.string.please_enter_first_name));
                }

            }
        });
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
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

                if (firstName.trim().length() == 0) {
                    etFirstName.setError(getResources().getString(
                            R.string.error_name_empty));
                    return;
                }
                if (UserORM.checkContainName(applicationContext, firstName)) {

                    etFirstName.setError(getResources().getString(
                            R.string.error_name_exist));

                    return;
                }

                if (!emailValidator.validate(email) && email.length() > 0) {
                    etEmail.setError(getResources().getString(
                            R.string.error_invalid_email));
                    return;
                }

                if (password.length() < MIN_COUNT_PASSWORD_SIZE
                        && password.length() > 0) {
                    etPassword.setError(getResources().getString(
                            R.string.error_invalid_password));

                    return;
                } else if (email.length() <= 0 && password.length() > 0) {
                    etEmail.setError(getResources().getString(
                            R.string.error_invalid_email));
                    return;
                }

                UserORM.insertUser(RegisterActivity.this, new UserORM(
                        firstName, lastName, sex, password, email));

                Intent intent = new Intent();
                intent.putExtra("login", firstName);
                setResult(RESULT_OK, intent);

                finish();

            }
        });

    }

    public class EmailValidator {

        private Pattern pattern;
        private Matcher matcher;

        private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        public EmailValidator() {
            pattern = Pattern.compile(EMAIL_PATTERN);
        }

        /**
         * Validate hex with regular expression
         *
         * @param hex hex for validation
         * @return true valid hex, false invalid hex
         */
        public boolean validate(final String hex) {

            matcher = pattern.matcher(hex);
            return matcher.matches();

        }
    }

}
