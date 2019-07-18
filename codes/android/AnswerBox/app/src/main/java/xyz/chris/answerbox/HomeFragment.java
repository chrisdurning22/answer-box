package xyz.cathal.answerbox;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This Fragment represents the Home screen.
 */

public class HomeFragment extends Fragment implements OnBackPressedListener {

    private BrowseAnswersCommunicator mFragmentCommunicator;
    private Spinner mSubjectSpinner;
    private Spinner mYearSpinner;
    private Haha ha;

    private String mSubject;
    private String mYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setOnBackPressedListener(this);
        getActivity().setTitle("Home");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragmentCommunicator = (BrowseAnswersCommunicator) getActivity();
        selectSubject();
        selectYear();

        ha = new Haha();
        ha.

        Button higherButton = (Button) getActivity().findViewById(R.id.higher_level_button);
        higherButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CheckConnectionTask task = new CheckConnectionTask(1);
                task.execute((Void) null);
            }
        });

        Button ordinaryButton = (Button) getActivity().findViewById(R.id.ordinary_level_button);
        ordinaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckConnectionTask task = new CheckConnectionTask(0);
                task.execute((Void) null);
            }
        });
    }

    /**
     * Fetches the subject from the subject spinner.
     */
    public void selectSubject() {
        mSubjectSpinner = (Spinner) getActivity().findViewById(R.id.subject_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.subjects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        mSubjectSpinner.setAdapter(adapter);

        mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubject = mSubjectSpinner.getItemAtPosition(mSubjectSpinner
                        .getSelectedItemPosition()).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Fetches the year from the year spinner.
     */
    public void selectYear() {
        mYearSpinner = (Spinner) getActivity().findViewById(R.id.year_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.year, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        mYearSpinner.setAdapter(adapter);

        mYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mYear = mYearSpinner.getItemAtPosition(
                        mYearSpinner.getSelectedItemPosition()).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPress() {}

    /**
     * Represents an asynchronous task used to check for an active internet connection.
     */
    private class CheckConnectionTask extends AsyncTask<Void, Void, Boolean> {

        private int mLevel;

        CheckConnectionTask(int level) {
            this.mLevel = level;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return NetworkUtility.isConnected();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean response) {
            // If connected to internet
            if (response) {
                mFragmentCommunicator.sendToBrowseAnswers(Integer.parseInt(mYear), mSubject, mLevel);
            } else {
                Toast.makeText(getActivity(), getString(R.string.offline_disabled),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Allows communication to BrowseAnswers.
     */

    interface BrowseAnswersCommunicator {
        void sendToBrowseAnswers(int year, String subject, int level);
    }
}