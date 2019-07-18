package xyz.cathal.answerbox;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This activity allows the user to change their password.
 *
 * @author Cathal Conroy
 */

public class PasswordActivity extends AppCompatActivity {

    private ChangePasswordTask mTask;
    private EditText mOldPassView;
    private EditText mNewPassView;
    private EditText mConfPassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        mOldPassView = (EditText) findViewById(R.id.text_change_pass_old);
        mNewPassView = (EditText) findViewById(R.id.text_change_pass_new);
        mConfPassView = (EditText) findViewById(R.id.text_change_pass_confirm);

        Button button = (Button) findViewById(R.id.button_change_pass);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });
    }

    /**
     * Attempts to change the users password by first ensuring the various value inputs are valid,
     * and then making a request to the server.
     */
    private void attemptChangePassword() {
        if (mTask != null) {
            return;
        }

        mOldPassView.setError(null);
        mNewPassView.setError(null);
        mConfPassView.setError(null);

        String oldPass = mOldPassView.getText().toString();
        String newPass = mNewPassView.getText().toString();
        String confPass = mConfPassView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check old password
        if (!InputValidator.isPasswordValid(oldPass)) {
            mOldPassView.setError(getString(R.string.error_password_short));
            focusView = mOldPassView;
            cancel = true;
        }

        // Check new password
        if (!InputValidator.isPasswordValid(newPass)) {
            mNewPassView.setError(getString(R.string.error_password_short));
            focusView = mNewPassView;
            cancel = true;
        }

        // Check conf password
        if (!newPass.equals(confPass)) {
            mConfPassView.setError(getString(R.string.error_password_match));
            focusView = mConfPassView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mTask = new ChangePasswordTask(oldPass, newPass);
            mTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous task used to change the users password.
     */

    private class ChangePasswordTask extends AsyncTask<Void, Void, Response> {

        private String mOldPass;
        private String mNewPass;

        ChangePasswordTask(String oldPass, String newPass) {
            this.mOldPass = oldPass;
            this.mNewPass = newPass;
        }

        @Override
        protected Response doInBackground(Void... params) {
            try {
                if (!NetworkUtility.isConnected()) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
            String session = settings.getString("session", null);
            return new ChangePasswordRequest(session, mOldPass, mNewPass).response;
        }

        @Override
        protected void onPostExecute(Response response) {
            mTask = null;

            if (response == null) {
                Toast.makeText(PasswordActivity.this, getString(R.string.offline_disabled),
                        Toast.LENGTH_SHORT).show();
                finish();
            } else if (response.success) {
                Toast.makeText(PasswordActivity.this, getString(R.string.password_updated),
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                switch (response.error) {
                    case "password_incorrect":
                        mOldPassView.setError(getString(R.string.error_password_incorrect));
                        mOldPassView.requestFocus();
                        break;

                    default:
                        Toast.makeText(PasswordActivity.this, getString(R.string.error_unknown),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }
}
