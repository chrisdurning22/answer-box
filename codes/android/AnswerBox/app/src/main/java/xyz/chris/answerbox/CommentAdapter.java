package xyz.cathal.answerbox;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * This Adapter acts as a bridge between the comment data (an ArrayList of Comments) and the View
 * which holds the data.
 *
 * @author Christopher Durning
 */

class CommentAdapter extends ArrayAdapter {

    private ArrayList<Comment> mComments;
    private Context mContext;
    private DeleteCommentTask mDeleteCommentTask;
    private ListView mDisplayCommentsView;
    private Activity mActivity;

    CommentAdapter(Context context, int textViewResourceId, ArrayList<Comment> objects, Activity activity) {
        super(context, textViewResourceId, objects);
        this.mComments = objects;
        this.mContext = context;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.comment_row_item, parent, false);
        }

        final Comment comment = mComments.get(position);

        ImageButton deleteComment = (ImageButton) v.findViewById(R.id.delete_comment_button);
        deleteComment.setVisibility(View.GONE);
        deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteCommentTask = new DeleteCommentTask(mContext, comment.id);
                mDeleteCommentTask.execute((Void) null);

                mComments.remove(comment);
                notifyDataSetChanged();
            }
        });

        final SharedPreferences settings = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        int currentUserId = settings.getInt("id", -1);

        // Only makes delete button visible for comments made by current user
        if (comment.user.id == currentUserId) {
            deleteComment.setVisibility(View.VISIBLE);
        }

        ((TextView) v.findViewById(R.id.comment_author_name)).setText(comment.user.username);
        ((TextView) v.findViewById(R.id.comment_content)).setText(comment.content);

        return v;
    }

    /**
     * Represents an asynchronous request to delete a comment.
     */
    private class DeleteCommentTask extends AsyncTask<Void, Void, Response> {

        private int mCommentId;
        private Context mContext;

        DeleteCommentTask(Context context, int commentId) {
            this.mContext = context;
            this.mCommentId = commentId;
        }

        @Override
        protected Response doInBackground(Void... params) {
            SharedPreferences settings = mContext.getSharedPreferences("pref", MODE_PRIVATE);
            String session = settings.getString("session", null);

            DeleteCommentRequest request = new DeleteCommentRequest(session, mCommentId);
            return request.response;
        }

        @Override
        protected void onPostExecute(Response response) {
            if (response.success) {
                Toast.makeText(mContext, "Your comment has been removed", Toast.LENGTH_SHORT)
                        .show();
            } else {
                switch (response.error) {
                    case "unauthorized":
                        Toast.makeText(mContext, mContext.getString(R.string.error_unauthorized),
                                Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(mContext, mContext.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
