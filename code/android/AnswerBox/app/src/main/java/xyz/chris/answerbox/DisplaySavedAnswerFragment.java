package xyz.cathal.answerbox;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplaySavedAnswerFragment extends Fragment implements OnBackPressedListener{
    private Answer mAnswer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setOnBackPressedListener(this);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_saved_answer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // display title
        TextView titleView = (TextView) getActivity().findViewById(R.id.display_saved_title);
        titleView.setText(mAnswer.title);

        // display content
        TextView contentView = (TextView) getActivity().findViewById(R.id.display_saved_content);
        contentView.setText(mAnswer.content);

        ImageButton removeAnswer = (ImageButton) getActivity().findViewById(R.id.remove_saved_answer);
        removeAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets the data repository in write mode
                AnswersDatabaseHelper helper = new AnswersDatabaseHelper(getActivity());
                SQLiteDatabase db = helper.getWritableDatabase();

                String selection1 = AnswersDatabaseContract.SolutionsTable.COLUMN_NAME_ID + " = ?";
                String[] selectionArgs1 = {Integer.toString(mAnswer.id)};

                db.delete(AnswersDatabaseContract.SolutionsTable.TABLE_NAME, selection1,
                        selectionArgs1);
                ((MainActivity) getActivity()).popFragment();

            }
        });

        AnswersDatabaseHelper helper = new AnswersDatabaseHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();

        String query = AnswersDatabaseContract.FilesTable.TABLE_NAME
                + " inner join solutions_files "
                + "on solutions_files.file_id = files.id "
                + "where solution_id = "
                + mAnswer.id;

        ArrayList<File> files = new ArrayList<>();
        Cursor cursor = db.query(query, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            File file = new File();
            file.id = cursor.getInt(0);
            file.hash = cursor.getString(1);
            file.extension = cursor.getString(2);
            files.add(file);
        }
        cursor.close();

        LinearLayout linearLayout = (LinearLayout) getActivity()
                .findViewById(R.id.saved_thumbnail_drawer);

        for (File file : files) {
            ImageView imageView = new ImageView(getActivity());
            java.io.File storageDir = getActivity().getExternalFilesDir("downloads");
            final java.io.File realFile = new java.io.File(storageDir.getPath() + "/"
                    + file.getFileName());
            Bitmap bitmap = BitmapFactory.decodeFile(realFile.getPath());
            final Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 250, 250);
            imageView.setImageBitmap(thumbnail);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    intent.setData(Uri.fromFile(realFile));
                    startActivity(intent);
                }
            });
            linearLayout.addView(imageView);
        }
    }

    /**
     * @param answer Displays Answer saved by the user
     */
    public void receiveObjectFromSavedAnswerFragment(Answer answer) {
        this.mAnswer = answer;
    }

    @Override
    public void onBackPress() {
        ((MainActivity) getActivity()).popFragment();
    }


}
