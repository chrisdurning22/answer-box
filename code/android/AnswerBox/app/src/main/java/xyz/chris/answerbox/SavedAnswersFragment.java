package xyz.cathal.answerbox;

import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A fragment that displays a list of saved Answers by binding to an ArrayList of Answers.
 */

public class SavedAnswersFragment extends ListFragment implements OnBackPressedListener{

    private ArrayList<Answer> mAnswers = new ArrayList<>();
    private DisplaySavedAnswerCommunicator mDisplaySavedAnswerCommunicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setOnBackPressedListener(this);
        getActivity().setTitle("Saved answers");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_answers, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateList();
        mDisplaySavedAnswerCommunicator = (DisplaySavedAnswerCommunicator) getActivity();
    }

    /**
     * Queries the offline SQLite3 database and provides the adapter with data.
     */
    private void populateList() {
        AnswersDatabaseHelper helper = new AnswersDatabaseHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();

        String MY_QUERY = AnswersDatabaseContract.SolutionsTable.TABLE_NAME + " INNER JOIN " +
                AnswersDatabaseContract.UsersTable.TABLE_NAME + " ON " +
                AnswersDatabaseContract.SolutionsTable.TABLE_NAME + "." +
                AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_USER_ID + " = " +
                AnswersDatabaseContract.UsersTable.TABLE_NAME + "." +
                AnswersDatabaseContract.UsersTable.COLUMN_NAME_ID;

        Cursor cursor = db.query(
                MY_QUERY,
                null,
                null,
                null,
                null,
                null,
                null
        );

        mAnswers.clear();
        while(cursor.moveToNext()) {

            Answer answer = new Answer();

            answer.id = Integer.parseInt(cursor.getString(0));
            answer.user_id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                            AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_USER_ID));
            answer.title = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_TITLE));
            answer.content = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_CONTENT));
            answer.reputation = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                            AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_REPUTATION));
            answer.subject_id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                            AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_SUBJECT_ID));
            answer.year = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                            AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_YEAR));
            answer.level = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                            AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_LEVEL));

            answer.user = new User();


            answer.user.id = Integer.parseInt(cursor.getString(8));
            answer.user.username = cursor.getString(cursor.getColumnIndexOrThrow(
                    AnswersDatabaseContract.UsersTable.COLUMN_NAME_USERNAME));
            answer.user.reputation = cursor.getInt(cursor.getColumnIndexOrThrow(
                    AnswersDatabaseContract.UsersTable.COLUMN_NAME_REPUTATION));
            mAnswers.add(answer);

        }
        cursor.close();
        SavedAnswerAdapter mAdapter = new SavedAnswerAdapter(getActivity(), R.layout.saved_row_item, mAnswers);
        this.setListAdapter(mAdapter);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mDisplaySavedAnswerCommunicator.sendObjectToDisplaySavedAnswerFragment(mAnswers.get(position));
    }

    @Override
    public void onBackPress() {
        // do nothing
    }

    interface DisplaySavedAnswerCommunicator {
        void sendObjectToDisplaySavedAnswerFragment(Answer answer);
    }
}
