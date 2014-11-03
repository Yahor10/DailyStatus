package by.android.dailystatus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import by.android.dailystatus.dialog.ForgotPasswordDialog;
import by.android.dailystatus.interfaces.FragmentActivityCallback;
import by.android.dailystatus.orm.model.UserORM;
import by.android.dailystatus.preference.PreferenceUtils;

public class LoginActivity extends ActionBarActivity implements FragmentActivityCallback, TextWatcher {

    // private Spinner mCountView;

    EditText loginEdit;
    EditText passwordEdit;
    TextView txtForgotPassword;

    private boolean passwordWasChanged = false;

    private List<UserORM> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().hide();

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
        // Typeface type =
        // Typeface.createFromAsset(getAssets(),"fonts/segoe-ui-1361529660.ttf");
        // welcomeText.setTypeface(type);

        loginEdit = (EditText) findViewById(R.id.edtLogin);
        loginEdit.setText(PreferenceUtils.getCurrentUser(this));
        loginEdit.addTextChangedListener(this);

        loginEdit.clearFocus();
        passwordEdit = (EditText) findViewById(R.id.edtPassword);
//        passwordEdit.setText(PreferenceUtils.getPassword(this));
        passwordEdit.addTextChangedListener(this);
        txtForgotPassword = (TextView) findViewById(R.id.txt_forgot_password);
        txtForgotPassword.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtForgotPassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isOnline(getApplicationContext())) {

                    UserORM user = UserORM.getUserByName(
                            getApplicationContext(), loginEdit.getText()
                                    .toString());
                    if (user == null) {
                        Toast.makeText(LoginActivity.this,
                                R.string.error_name_does_not_exist,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ForgotPasswordDialog dialog = new ForgotPasswordDialog(
                                LoginActivity.this, user);
                        dialog.setListener(LoginActivity.this);
                        dialog.show(getSupportFragmentManager(), "");
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.error_internet_off, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        findViewById(R.id.btnOk).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String loginStr = loginEdit.getText().toString();
                String passwordStr = passwordEdit.getText().toString();

//                PreferenceUtils.savePassword(LoginActivity.this, passwordStr);

                if (loginStr.length() != 0) {
                    boolean containNameFlag = false;
                    for (UserORM user : allUsers) {
                        if (user.name.equals(loginStr)) {
                            containNameFlag = true;
                            if (user.password.equals(passwordStr) || !passwordWasChanged) {
                                PreferenceUtils.setCurrentUser(getApplicationContext(), loginStr);
                                startActivity(MainActivity.buildIntent(getApplicationContext()));
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
        if (data == null) {
            return;
        }
        String loginStr = data.getStringExtra("login");
        PreferenceUtils.setCurrentUser(getApplicationContext(), loginStr);
        startActivity(MainActivity.buildIntent(getApplicationContext()));
        finish();

    }

    public static boolean isOnline(Context cont) {
        ConnectivityManager cm = (ConnectivityManager) cont
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        allUsers = UserORM.getAllFirstNames(this);
        super.onResume();

    }

    @Override
    public void callToActivity(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        passwordWasChanged = true;
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
