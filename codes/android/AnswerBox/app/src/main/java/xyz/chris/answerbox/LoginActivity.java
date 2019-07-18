package xyz.cathal.answerbox;

import android.content.Intent;
import android.content.SharedPreferences;
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
 * This Activity is the entry point of the application. If the user has a session key, it will be
 * used for authentication. Otherwise, a login form will display to allow the user to attempt to
 * authenticate with credentials.
 *
 * @author Cathal Conroy
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mTask = null;
    private SharedPreferences mSettings = null;

    private AutoCompleteTextView mIdentifierView;
    private EditText mPasswordView;
    private String mSession = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettings = getSharedPreferences("pref", MODE_PRIVATE);

        if (mSettings.contains("session")) {
            attemptSessionLogin();
        } else {
            setContentView(R.layout.activity_login);
        }

        mIdentifierView = (AutoCompleteTextView) findViewById(R.id.identifier);

        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView textView = (TextView) findViewById(R.id.need_account);
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Attempts to authenticate the user with a session key.
     */
    private void attemptSessionLogin() {
        mSettings = getSharedPreferences("pref", MODE_PRIVATE);
        if (mSettings.contains("session")) {
            mSession = mSettings.getString("session", null);
            mTask = new UserLoginTask(mSession);
            mTask.execute((Void) null);
            try {
                mTask.get();
            } catch (Exception e) {
                //
            }
        }

        setContentView(R.layout.activity_login);
    }

    /**
     * Attempts to authenticate the user with a username/email and password.
     */
    private void attemptLogin() {
        if (mTask != null) {
            return;
        }

        mIdentifierView.setError(null);
        mPasswordView.setError(null);

        String identifier = mIdentifierView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if identifier is valid
        if (TextUtils.isEmpty(identifier)) {
            mIdentifierView.setError(getString(R.string.error_field_required));
            focusView = mIdentifierView;
            cancel = true;
        } else if (!InputValidator.isUsernameValid(identifier) &&
                !InputValidator.isEmailValid(identifier)) {
            mIdentifierView.setError(getString(R.string.error_username_invalid));
            focusView = mIdentifierView;
            cancel = true;
        }

        // Check if password is valid
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (password.length() <= 5) {
            mPasswordView.setError(getString(R.string.error_password_short));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mTask = new UserLoginTask(identifier, password);
            mTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, LoginResponse> {

        private String mIdentifier = null;
        private String mPassword = null;
        private String mSession = null;

        UserLoginTask(String session) {
            mSession = session;
        }

        UserLoginTask(String identifier, String password) {
            this.mIdentifier = identifier;
            this.mPassword = password;
        }

        @Override
        protected LoginResponse doInBackground(Void... params) {
            LoginRequest loginRequest;

            try {
                if (!NetworkUtility.isConnected()) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }

            if (mSession != null) {
                loginRequest = new LoginRequest(mSession);
            } else {
                loginRequest = new LoginRequest(mIdentifier, mPassword);
            }

            return (LoginResponse) loginRequest.response;
        }

        @Override
        protected void onPostExecute(final LoginResponse response) {
            mTask = null;
            SharedPreferences.Editor editor = mSettings.edit();
            Intent intent;

            // If response is null then we are not connected to the internet
            if (response == null) {
                editor = mSettings.edit();
                editor.putBoolean("offline", true);
                editor.apply();

                /*
                 * If session is set then we assume their login is valid. They can't do any damage
                 * anyway due to being in offline mode
                 */
                if (mSession != null) {
                    Toast.makeText(LoginActivity.this, getString(R.string.offline_mode),
                            Toast.LENGTH_SHORT).show();
                    intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Active internet required to sign in",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // response will never be null, as it is checked above
            if (response.success) {
                editor.putString("session", response.user.session);
                editor.putInt("id", response.user.id);
                editor.putString("username", response.user.username);
                editor.apply();
                intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                switch (response.error) {
                    case "user_missing":
                        mIdentifierView.setError(getString(R.string.error_user_missing));
                        mIdentifierView.requestFocus();
                        break;

                    case "password_incorrect":
                        mPasswordView.setError(getString(R.string.error_password_incorrect));
                        mPasswordView.requestFocus();
                        break;

                    case "session_missing":
                        editor.remove("session");
                        editor.commit();
                        Toast.makeText(LoginActivity.this, R.string.error_session_missing,
                                Toast.LENGTH_SHORT).show();
                        intent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    default:
                        Toast.makeText(LoginActivity.this, getString(R.string.error_unknown),
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