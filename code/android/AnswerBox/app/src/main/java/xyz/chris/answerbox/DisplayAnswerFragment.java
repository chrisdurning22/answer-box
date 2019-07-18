package xyz.cathal.answerbox;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * This Fragment displays a solution chosen by the user.
 *
 * @author Christopher Durning
 */

public class DisplayAnswerFragment extends Fragment implements OnBackPressedListener {

    private FileDownloadTask mFileTask;
    private SubmitCommentTask mCommentTask;
    private ReputationTask mReputationTask;
    private ReportTask mReportTask;
    private Answer mAnswer;
    private TextView mDisplayVotesView;
    private EditText mComment;
    private int mLatestCommentId;
    private ArrayList<Comment> mCommentList;
    private CommentAdapter mCommentAdapter;
    private ArrayList<xyz.cathal.answerbox.File> mFiles;
    private Boolean hasUpVoted = false;
    private Boolean hasDownVoted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFileTask = new FileDownloadTask(mAnswer.files);
        mFileTask.execute((Void) null);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_answer, container, false);
        ((MainActivity) getActivity()).setOnBackPressedListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView titleView = (TextView) getActivity().findViewById(R.id.display_answers_title);
        titleView.setText(mAnswer.title);

        ImageButton reportButton = (ImageButton) getActivity().findViewById(R.id.report_answer);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptReport();
            }
        });

        TextView contentView = (TextView) getActivity().findViewById(R.id.display_answers_content);
        contentView.setText(mAnswer.content);

        mDisplayVotesView = (TextView) getActivity().findViewById(R.id.votes);
        mDisplayVotesView.setText(String.valueOf(mAnswer.reputation));

        final ColorStateList defaultTextColor = mDisplayVotesView.getTextColors();

        ImageButton upVoteButton = (ImageButton) getActivity().findViewById(R.id.up_button);
        upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value;

                if (!hasUpVoted && !hasDownVoted) {
                    mAnswer.reputation++;
                    hasUpVoted = true;
                    mDisplayVotesView.setTextColor(Color.GREEN);
                    value = 1;
                } else if (hasDownVoted) {
                    mAnswer.reputation += 2;
                    hasDownVoted = false;
                    hasUpVoted = true;
                    mDisplayVotesView.setTextColor(Color.GREEN);
                    value = 1;
                } else {
                    mAnswer.reputation--;
                    hasUpVoted = false;
                    hasDownVoted = false;
                    mDisplayVotesView.setTextColor(defaultTextColor);
                    value = 0;
                }

                mReputationTask = new ReputationTask(value, mAnswer.id);
                mReputationTask.execute((Void) null);
                mDisplayVotesView.setText(String.valueOf(mAnswer.reputation));
            }
        });

        ImageButton downVoteButton = (ImageButton) getActivity().findViewById(R.id.down_button);
        downVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value;

                // As above
                if (!hasUpVoted && !hasDownVoted) {
                    mAnswer.reputation--;
                    hasDownVoted = true;
                    mDisplayVotesView.setTextColor(Color.RED);
                    value = -1;
                } else if (hasUpVoted) {
                    mAnswer.reputation -= 2;
                    hasDownVoted = true;
                    hasUpVoted = false;
                    mDisplayVotesView.setTextColor(Color.RED);
                    value = -1;
                } else {
                    mAnswer.reputation++;
                    hasDownVoted = false;
                    hasUpVoted = false;
                    mDisplayVotesView.setTextColor(defaultTextColor);
                    value = 0;
                }

                mReputationTask = new ReputationTask(value, mAnswer.id);
                mReputationTask.execute((Void) null);
                mDisplayVotesView.setText(String.valueOf(mAnswer.reputation));
            }
        });

        if (mAnswer.vote > 0) {
            mDisplayVotesView.setTextColor(Color.GREEN);
            hasUpVoted = true;
            hasDownVoted = false;
        } else if (mAnswer.vote < 0) {
            mDisplayVotesView.setTextColor(Color.RED);
            hasUpVoted = false;
            hasDownVoted = true;
        }

        // display authors name
        ((TextView) getActivity().findViewById(R.id.answer_author)).setText(mAnswer.user.username);

        // save answer to database
        ImageButton saveAnswerButton = (ImageButton) getActivity().findViewById(R.id.save_answer_to_database);
        saveAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Gets the data repository in write mode
                AnswersDatabaseHelper helper = new AnswersDatabaseHelper(getActivity());
                SQLiteDatabase db = helper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues valuesSolution = new ContentValues();
                valuesSolution.put(AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_ID, mAnswer.id);
                valuesSolution.put(AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_USER_ID, mAnswer.user_id);
                valuesSolution.put(AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_TITLE, mAnswer.title);
                valuesSolution.put(AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_CONTENT, mAnswer.content);
                valuesSolution.put(AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_REPUTATION, mAnswer.reputation);
                valuesSolution.put(AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_SUBJECT_ID, mAnswer.subject_id);
                valuesSolution.put(AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_YEAR, mAnswer.year);
                valuesSolution.put(AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_LEVEL, mAnswer.level);

                // Insert new row of solutions data, returning the primary key value of the new row
                db.insert(AnswersDatabaseContract.SolutionsTable.TABLE_NAME, null, valuesSolution);

                // As above
                ContentValues valuesUser = new ContentValues();
                valuesUser.put(AnswersDatabaseContract.UsersTable.COLUMN_NAME_ID, mAnswer.user.id);
                valuesUser.put(AnswersDatabaseContract.UsersTable.COLUMN_NAME_USERNAME, mAnswer.user.username);
                valuesUser.put(AnswersDatabaseContract.UsersTable.COLUMN_NAME_REPUTATION, mAnswer.user.reputation);
                db.insert(AnswersDatabaseContract.UsersTable.TABLE_NAME, null, valuesUser);

                // As above
                ContentValues valuesFile = new ContentValues();
                ContentValues valuesSolutionsFiles = new ContentValues();

               for (xyz.cathal.answerbox.File file : mFiles) {
                   int fileID = file.id;
                   String hash = file.hash;
                   String extension = file.extension;

                   // Check if file exists in db before inserting
                   int count;
                   Cursor c = null;

                   String query = "select count(*) from files where hash = ?";
                   c = db.rawQuery(query, new String[] {hash});
                   if (c.moveToFirst()) {
                       count = c.getInt(0);
                   } else {
                       count = 0;
                   }

                   c.close();

                   if (count == 0) {
                       // As above
                       valuesFile.put(AnswersDatabaseContract.FilesTable.COLUMN_NAME_ID, fileID);
                       valuesFile.put(AnswersDatabaseContract.FilesTable.COLUMN_NAME_HASH, hash);
                       valuesFile.put(AnswersDatabaseContract.FilesTable.COLUMN_NAME_EXTENSION, extension);
                       db.insert(AnswersDatabaseContract.FilesTable.TABLE_NAME, null, valuesFile);

                       // As above
                       valuesSolutionsFiles.put(AnswersDatabaseContract.SolutionsFilesTable.COLUMN_NAME_SOLUTION_ID, mAnswer.id);
                       valuesSolutionsFiles.put(AnswersDatabaseContract.SolutionsFilesTable.COLUMN_NAME_FILE_ID, fileID);
                       db.insert(AnswersDatabaseContract.SolutionsFilesTable.TABLE_NAME, null, valuesSolutionsFiles);
                   }
               }
               Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        mCommentList = new ArrayList<>(Arrays.asList(mAnswer.comments));
        mCommentAdapter = new CommentAdapter(this.getActivity(),R.layout.comment_row_item, mCommentList, getActivity());
        ((ListView) getActivity().findViewById(R.id.comments_list)).setAdapter(mCommentAdapter);


        mComment = (EditText) getActivity().findViewById(R.id.add_comment);
        getActivity().findViewById(R.id.submit_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptComment();


                Log.i("hey", mLatestCommentId + "");

                SharedPreferences settings = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                // displays temporary comment
                int id = settings.getInt("id", -1);
                String username = settings.getString("username", null);
                String comment = mComment.getText().toString();

                Comment newComment = new Comment();
                newComment.user = new User();
                newComment.user.id = id;
                newComment.user.username = username;
                newComment.content = comment;

                GetCommentTask getCommentTask = new GetCommentTask(newComment);
                getCommentTask.execute((Void) null);
            }
        });
    }

    @Override
    public void onBackPress() {
        ((MainActivity) getActivity()).popFragment();
    }

    /**
     * @param answer Answer object chosen by the user from the list in BrowseAnswerFragment
     */
    public void receiveObject(Answer answer) {
        this.mAnswer = answer;
    }

    /**
     * Sends ID of reported Answer to server
     */
    private void attemptReport() {
        if (mReportTask != null) {
            return;
        }

        mReportTask = new ReportTask(mAnswer.id);
        mReportTask.execute((Void) null);
    }

    /**
     * Sends new comment to server
     */
    private void attemptComment() {
        if (mCommentTask != null) {
            return;
        }

        mComment.setError(null);

        String comment = mComment.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if comment is valid
        if (TextUtils.isEmpty(comment)) {
            mComment.setError(getString(R.string.error_field_required));
            focusView = mComment;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mCommentTask = new SubmitCommentTask(comment, mAnswer.id);
            mCommentTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous task used to report a solution
     */
    private class ReportTask extends AsyncTask<Void, Void, Response> {

        private int mSolutionId;

        ReportTask(int solutionId) {
            this.mSolutionId = solutionId;
        }

        @Override
        protected Response doInBackground(Void... params) {
            SharedPreferences settings = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
            String session = settings.getString("session", null);

            ReportRequest request = new ReportRequest(session, mSolutionId);
            return request.response;
        }

        @Override
        protected void onPostExecute(Response response) {
            mReportTask = null;

            if (response.success) {

                Toast.makeText(getActivity(), "Your report has been noted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_unknown),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Represents an asynchronous task used to retrieve the latest comment
     */
    private class GetCommentTask extends AsyncTask<Void, Void, CommentResponse> {

        private Comment mComment;

        GetCommentTask(Comment comment) {
            this.mComment = comment;
        }

        @Override
        protected CommentResponse doInBackground(Void... params) {
            SharedPreferences settings = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
            String session = settings.getString("session", null);

            GetCommentRequest request = new GetCommentRequest(session, -1);
            return (CommentResponse) request.response;
        }

        @Override
        protected void onPostExecute(CommentResponse response) {
            mReputationTask = null;

            if (response.success) {
                mLatestCommentId = response.comment.id;
                mComment.id = response.comment.id;
                mCommentList.add(mComment);
                mCommentAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
            mFileTask = null;
        }
    }

    /**
     * Represents an asynchronous task used to change the reputation of a solution
     */
    private class ReputationTask extends AsyncTask<Void, Void, Response> {

        private int mValue;
        private int mSolutionId;

        ReputationTask(int value, int solutionId) {
            this.mValue = value;
            this.mSolutionId = solutionId;
        }

        @Override
        protected Response doInBackground(Void... params) {
            SharedPreferences settings = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
            String session = settings.getString("session", null);

            ReputationRequest request = new ReputationRequest(session, mValue, mSolutionId);
            return request.response;
        }

        @Override
        protected void onPostExecute(Response response) {
            mReputationTask = null;

            if (response.success) {
                Toast.makeText(getActivity(), "Your vote has been noted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_unknown),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFileTask = null;
        }
    }

    /**
     * Represents an asynchronous task used to submit a comment
     */
    private class SubmitCommentTask extends AsyncTask<Void, Void, CommentResponse> {

        private String mBody;
        private int mSolutionId;
        private OnTaskCompleted listener;

        SubmitCommentTask(String body, int solutionId) {
            this.mBody = body;
            this.mSolutionId = solutionId;
        }

        @Override
        protected CommentResponse doInBackground(Void... params) {
            SharedPreferences settings = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
            String session = settings.getString("session", null);

            CommentRequest request = new CommentRequest(session, mBody, mSolutionId);
            return (CommentResponse) request.response;
        }

        @Override
        protected void onPostExecute(CommentResponse response) {
            mCommentTask = null;

            if (response.success) {
                Toast.makeText(getActivity(), "Your comment has been posted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_unknown),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFileTask = null;
        }
    }

    /**
     * Represents an asynchronous task used to download files
     */
    private class FileDownloadTask extends AsyncTask<Void, Void, FileDownloadResponse[]> {

        private xyz.cathal.answerbox.File[] mFiles;

        FileDownloadTask(xyz.cathal.answerbox.File[] files) {
            this.mFiles = files;
        }

        @Override
        protected FileDownloadResponse[] doInBackground(Void... params) {
            SharedPreferences settings = getActivity().getSharedPreferences("pref",
                    Context.MODE_PRIVATE);
            String session = settings.getString("session", null);
            FileDownloadResponse[] responses = new FileDownloadResponse[mFiles.length];

            for (int i = 0; i < mFiles.length; i++) {
                // Check if the file exists before attempting to download
                File storageDir = getActivity().getExternalFilesDir("downloads");
                File file = new File(storageDir.getPath() + "/" + mFiles[i].getFileName());
                if (file.exists() && !file.isDirectory()) {
                    responses[i] = new FileDownloadResponse();
                    responses[i].success = true;
                    responses[i].file = mFiles[i];
                } else {
                    int id = mFiles[i].id;

                    FileDownloadRequest request = new FileDownloadRequest(session, id);
                    responses[i] = (FileDownloadResponse) request.response;
                }
            }

            return responses;
        }

        @Override
        protected void onPostExecute(FileDownloadResponse[] responses) {
            mFileTask = null;
            boolean success = true;
            HashMap<String, File> paths = new HashMap<>();
            DisplayAnswerFragment.this.mFiles = new ArrayList<>();

            for (FileDownloadResponse response : responses) {
                if (!response.success) {
                    success = false;
                    break;
                } else {
                    File file = FileUtility.fromBase64(getActivity(), response.file);
                    paths.put(response.file.getFileName(), file);
                    DisplayAnswerFragment.this.mFiles.add(response.file);
                }
            }

            if (!success) {
                Toast.makeText(getActivity(), getString(R.string.error_unknown),
                        Toast.LENGTH_SHORT).show();
            }

            LinearLayout linearLayout = (LinearLayout) getActivity()
                    .findViewById(R.id.thumbnail_drawer);

            for (Map.Entry<String, File> entry : paths.entrySet()) {
                final File file = entry.getValue();

                ImageView imageView = new ImageView(getActivity());
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                final Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 250, 250);
                imageView.setImageBitmap(thumbnail);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ImageActivity.class);
                        intent.setData(Uri.fromFile(file));
                        startActivity(intent);
                    }
                });
                linearLayout.addView(imageView);
            }
        }
    }
}
