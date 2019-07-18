package xyz.cathal.answerbox;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A register screen that offers register via email/password.
 *
 * @author Cathal Conroy
 */
public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask mTask = null;
    private AutoCompleteTextView mUsernameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPassConfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsernameView = (AutoCompleteTextView) findViewById(R.id.choose_username);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.choose_email);
        mPasswordView = (EditText) findViewById(R.id.choose_password);
        mPassConfView = (EditText) findViewById(R.id.match_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register attempt is made.
     */
    private void attemptRegister() {
        if (mTask != null) {
            return;
        }

        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPassConfView.setError(null);

        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passConf = mPassConfView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!InputValidator.isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_username_invalid));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!InputValidator.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_email_invalid));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPassConfView.setError(getString(R.string.error_field_required));
            focusView = mPassConfView;
            cancel = true;
        }

        if (!InputValidator.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_password_invalid));
            Toast.makeText(this, "Password must be 8 characters long, with a number and letter",
                    Toast.LENGTH_LONG).show();
            focusView = mPasswordView;
            cancel = true;
        }

        // Check that password and confPass match
        if (!password.equals(passConf)) {
            mPassConfView.setError(getString(R.string.error_password_match));
            focusView = mPassConfView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mTask = new UserRegisterTask(username, email, password);
            mTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous task used to create a new account
     */
    private class UserRegisterTask extends AsyncTask<Void, Void, RegisterResponse> {

        private String mUsername;
        private String mEmail;
        private String mPassword;

        UserRegisterTask(String username, String email, String password) {
            mUsername = username;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected RegisterResponse doInBackground(Void... params) {
            RegisterRequest request = new RegisterRequest(mUsername, mEmail, mPassword);
            return (RegisterResponse) request.response;
        }

        @Override
        protected void onPostExecute(final RegisterResponse response) {
            mTask = null;

            if (response.success) {
                Toast.makeText(RegisterActivity.this, "You may now sign in",
                        Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            } else {
                switch (response.error) {
                    case "username_taken":
                        mUsernameView.setError(getString(R.string.error_username_taken));
                        mUsernameView.requestFocus();
                        break;

                    case "email_exists":
                        mEmailView.setError(getString(R.string.error_email_taken));
                        mEmailView.requestFocus();
                        break;

                    default:
                        Toast.makeText(RegisterActivity.this, getString(R.string.error_unknown),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }
}

