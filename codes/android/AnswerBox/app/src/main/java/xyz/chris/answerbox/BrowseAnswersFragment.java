package xyz.cathal.answerbox;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A fragment that displays a list of items by binding to an ArrayList of Answers.
 *
 * @author Cathal Conroy
 */

public class BrowseAnswersFragment extends ListFragment implements OnBackPressedListener, TaskListener {
    private int mYear;
    private String mSubject;
    private int mLevel;
    private BrowseAnswersTask mTask;
    private DisplayAnswersCommunicator mDisplayAnswersCommunicator;
    private ArrayList<Answer> mAnswers = new ArrayList<>();
    private AnswerAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setOnBackPressedListener(this);
        getActivity().invalidateOptionsMenu();
        return inflater.inflate(R.layout.fragment_browse_answers, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDisplayAnswersCommunicator = (DisplayAnswersCommunicator) getActivity();

        DisplayAnswerFragment displayAnswerFragment = new DisplayAnswerFragment();

        // Obtain a set of answers from the database
        mTask = new BrowseAnswersTask(this, mAdapter, mAnswers, mYear, mSubject, mLevel);
        mTask.setOnResultsListener(this);
        mTask.execute((Void) null);

        TextView textView = (TextView) getActivity().findViewById(R.id.level);
        String level = mLevel == 1 ? "Higher" : "Ordinary";
        textView.setText(level + " - " + mSubject + " - " + mYear);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mDisplayAnswersCommunicator.sendObjectToDisplayAnswersFragment(mAnswers.get(position));
    }

    @Override
    public void onBackPress() {
        ((MainActivity) getActivity()).popFragment();
    }

    /**
     * Receives data from the HomeFragment.
     *
     * @param year The year to be searched
     * @param subject The subject to be searched
     * @param level The level to be searched
     */
    public void receiveData(int year, String subject, int level) {
        mYear = year;
        mSubject = subject;
        mLevel = level;
    }

    /**
     * Receives data from the Adapter.
     *
     * @param answers The answers to be inserted into the list
     */
    public void onTaskCompleted(ArrayList<Answer> answers) {
        mAnswers = answers;
    }

    /**
     * Represents an asynchronous task used to search for answers.
     */

    private class BrowseAnswersTask extends AsyncTask<Void, Void, BrowseResponse> {
        private ListFragment mFragment;
        private ArrayList<Answer> mAnswers;
        private TaskListener mListener;
        private AnswerAdapter mAdapter;
        private int mYear;
        private String mSubject;
        private int mLevel;
        private SharedPreferences mSettings;

        BrowseAnswersTask(ListFragment f, AnswerAdapter adapter, ArrayList<Answer> answers, int year, String subject, int level) {
            this.mFragment = f;
            this.mAdapter = adapter;
            this.mYear = year;
            this.mSubject = subject;
            this.mLevel = level;
            this.mAnswers = answers;
            mSettings = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        }

        @Override
        protected BrowseResponse doInBackground(Void... params) {
            try {
                if (!NetworkUtility.isConnected()) {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }

            String session = mSettings.getString("session", null);
            BrowseRequest browseRequest = new BrowseRequest(session, mYear, mSubject, mLevel);
            return (BrowseResponse) browseRequest.response;
        }

        @Override
        protected void onPostExecute(BrowseResponse response) {
            mTask = null;

            // If not connected to internet
            if (response == null) {
                Toast.makeText(getActivity(), getString(R.string.offline_disabled),
                        Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            } else if (response.success) {
                // Only populate the adapter if there is data available
                if (response.answers.length > 0) {
                    mAnswers = new ArrayList<>(Arrays.asList(response.answers));
                    mSettings = getActivity().getSharedPreferences("answers", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSettings.edit();

                    // Store each answer in SharedPreferences
                    for (Answer answer : mAnswers) {
                        String value = mSubject + ":" + mYear + ":" + mLevel;
                        if (!mSettings.contains(String.valueOf(answer.id))) {
                            editor.putString(String.valueOf(answer.id), value);
                        }
                    }

                    editor.apply();
                    mAdapter = new AnswerAdapter(getActivity(), R.layout.row_item, mAnswers);
                    mFragment.setListAdapter(mAdapter);
                    mListener.onTaskCompleted(mAnswers);
                }
            } else {
                switch (response.error) {
                    case "session_missing":
                        Toast.makeText(getActivity(), R.string.error_session_missing,
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        break;

                    default:
                        Toast.makeText(getActivity(), getString(R.string.error_unknown),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

        /**
         * Sets the onClickListener.
         *
         * @param listener The listener to be used
         */
        void setOnResultsListener(TaskListener listener) {
            this.mListener = listener;
        }
    }

    /**
     * This interface is used to communicate with the DisplayAnswersFragment.
     */
    interface DisplayAnswersCommunicator {
        void sendObjectToDisplayAnswersFragment(Answer answer);
    }
}