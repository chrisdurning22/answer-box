package xyz.cathal.answerbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This Adapter is used to populate the list of saved answers with data.
 *
 * @author Christopher Durning
 */

class SavedAnswerAdapter extends ArrayAdapter {
    private ArrayList<Answer> mAnswers;

    SavedAnswerAdapter(Context context, int textViewResourceId, ArrayList<Answer> answers) {
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
            v = inflater.inflate(R.layout.saved_row_item, parent, false);
        }

        Answer answer = mAnswers.get(position);
        String title = answer.title;

        SharedPreferences settings = getContext().getSharedPreferences("answers",
                Context.MODE_PRIVATE);
        String prefResult = settings.getString(String.valueOf(answer.id), null);
        String[] results = prefResult.split(":");
        String subject = results[0];
        String year = results[1];
        String level = results[2].equals("1") ? "Higher level" : "Ordinary level";

        ((TextView) v.findViewById(R.id.row_title)).setText(title);
        ((TextView) v.findViewById(R.id.row_subject)).setText(subject);
        ((TextView) v.findViewById(R.id.row_year)).setText(year);
        ((TextView) v.findViewById(R.id.row_level)).setText(level);

        return v;
    }
}