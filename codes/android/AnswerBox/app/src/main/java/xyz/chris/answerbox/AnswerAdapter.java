package xyz.cathal.answerbox;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This Adapter is used to populate the list of solutions with data.
 *
 * @author Cathal Conroy
 */

class AnswerAdapter extends ArrayAdapter {

    private ArrayList<Answer> mAnswers;

    AnswerAdapter(Context context, int textViewResourceId, ArrayList<Answer> answers) {
        super(context, textViewResourceId, answers);
        this.mAnswers = answers;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_item, parent, false);
        }

        Answer answer = mAnswers.get(position);

        TextView reputationView = (TextView) v.findViewById(R.id.row_reputation);
        reputationView.setText(String.valueOf(answer.reputation));

        // Color positive reputation green, and negative red
        if (answer.reputation > 0) {
            reputationView.setTextColor(Color.parseColor("#4CAF50"));
        } else if (answer.reputation < 0) {
            reputationView.setTextColor(Color.parseColor("#E53935"));
        }

        TextView commentsView = (TextView) v.findViewById(R.id.row_comments);
        String comments = answer.comments.length + " comments";
        commentsView.setText(comments);

        ((TextView) v.findViewById(R.id.row_title)).setText(answer.title);

        return v;
    }
}
