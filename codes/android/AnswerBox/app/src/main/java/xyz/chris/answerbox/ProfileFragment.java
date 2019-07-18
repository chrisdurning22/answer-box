package xyz.cathal.answerbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * This Fragment allows the user to modify their account settings and update personal information.
 */

public class ProfileFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Profile");

        addPreferencesFromResource(R.xml.preferences);

        Preference logout = findPreference("logout");
        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LogoutTask task = new LogoutTask();
                task.execute((Void) null);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Represents an asynchronous task used to logout the user.
     */
    private class LogoutTask extends AsyncTask<Void, Void, Response> {

        private SharedPreferences settings;
        private SharedPreferences.Editor editor;

        @Override
        protected Response doInBackground(Void... params) {
            settings = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            String session = settings.getString("session", null);

            /*
             * Even if there is no internet connection, we can remove our session key and the server
             * will do the same once we attempt to login again.
             */
            try {
                if (!NetworkUtility.isConnected()) {
                    editor = settings.edit();
                    editor.remove("session");
                    editor.apply();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return null;
            }

            LogoutRequest logoutRequest = new LogoutRequest(session);
            return logoutRequest.response;
        }

        @Override
        protected void onPostExecute(Response response) {
            if (response == null) {
                return;
            }
            if (!response.success) {
                switch (response.error) {
                    case "not_logged_in":
                        Toast.makeText(getActivity(), R.string.error_not_logged_in,
                                Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(getActivity(), getString(R.string.error_unknown),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            if (settings.contains("session")) {
                editor = settings.edit();
                editor.remove("session");
                editor.apply();
            }

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}