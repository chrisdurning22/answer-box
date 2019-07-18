package xyz.cathal.answerbox;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This Activity allows the user to submit a new solution.
 *
 * @author Cathal Conroy
 */

public class SubmitAnswerActivity extends AppCompatActivity{

    private static final int REQUEST_OPEN_GALLERY = 1;
    private static final int REQUEST_TAKE_PICTURE = 2;

    private SubmitAnswerTask mTask;
    private EditText mTitleView;
    private EditText mBodyView;
    private ArrayList<String> mImagePaths;
    private boolean mSuccess = true;
    private int mYear;
    private String mSubject;
    private int mLevel;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_answer);

        Bundle bundle = getIntent().getExtras();
        mSubject = bundle.getString("SUBJECT_ID");
        mYear = bundle.getInt("YEAR_ID");
        mLevel = bundle.getInt("LEVEL_ID");

        mImagePaths = new ArrayList<>();

        mTitleView = (EditText) findViewById(R.id.text_create_answer_title);
        mBodyView = (EditText) findViewById(R.id.text_create_answer_body);

        Button cameraButton = (Button) findViewById(R.id.button_open_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Button imagesButton = (Button) findViewById(R.id.button_select_images);
        imagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, REQUEST_OPEN_GALLERY);
            }
        });

        Button submitButton = (Button) findViewById(R.id.button_submit_solution);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_OPEN_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {

            // Single image, else multiple images
            if (data.getData() != null) {
                String path = getRealPathFromUri(this, data.getData());
                mImagePaths.add(path);
            } else if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    String path = getRealPathFromUri(this, item.getUri());
                    mImagePaths.add(path);
                }
            }
        } else if (requestCode == REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {
            String path = new java.io.File(mImageUri.getPath()).getPath();
            mImagePaths.add(path);
        }
        Toast.makeText(this, mImagePaths.size() + " image(s) selected", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Call Android's default camera application to handle the image capture.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                mImageUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
            } else {
                Toast.makeText(this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creates a new file to be filled with image data later.
     *
     * @return The newly created File
     * @throws IOException Thrown if the file cannot be created
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("downloads");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    /**
     * Resolves the real path from a URI.
     *
     * @param context The calling context
     * @param uri The URI to be resolved
     * @return The URI's real path
     */
    private String getRealPathFromUri(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // Where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
                        new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * Attempts to submit the new solution, checking for any errors along the way.
     */
    private void attemptSubmit() {
        if (mTask != null) {
            return;
        }

        mTitleView.setError(null);
        mBodyView.setError(null);

        String title = mTitleView.getText().toString();
        String body = mBodyView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid title
        if (TextUtils.isEmpty(title)) {
            mTitleView.setError(getString(R.string.error_field_required));
            focusView = mTitleView;
            cancel = true;
        } else if (!InputValidator.isTitleValid(title)) {
            mTitleView.setError(getString(R.string.error_title_short));
            focusView = mTitleView;
            cancel = true;
        }

        // Check for a valid body
        if (TextUtils.isEmpty(body)) {
            mBodyView.setError(getString(R.string.error_field_required));
            focusView = mBodyView;
            cancel = true;
        } else if (!InputValidator.isBodyValid(body)) {
            mBodyView.setError(getString(R.string.error_body_short));
            focusView = mBodyView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
            String session = settings.getString("session", null);
            mTask = new SubmitAnswerTask(session, title, body);
            mTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous task used to submit a new solution.
     */
    private class SubmitAnswerTask extends AsyncTask<Void, Void, Response[]> {

        private String mTitle;
        private String mBody;
        private String mSession;

        SubmitAnswerTask(String session, String title, String body) {
            this.mSession = session;
            this.mTitle = title;
            this.mBody = body;
        }

        @Override
        protected Response[] doInBackground(Void... params) {
            Response[] responses = new Response[1 + mImagePaths.size()];
            SubmitAnswerRequest request = new SubmitAnswerRequest(mSession, mTitle, mBody, mSubject,
                    mYear, mLevel);
            SubmitAnswerResponse response = (SubmitAnswerResponse) request.response;
            responses[0] = response;

            for (int i = 1; i < mImagePaths.size() + 1; i++) {
                java.io.File file = new File(mImagePaths.get(i - 1));
                FileUploadRequest fileUploadRequest = new FileUploadRequest(mSession, file,
                        response.answer.id);
                responses[i] = fileUploadRequest.response;
            }

            return responses;
        }

        @Override
        protected void onPostExecute(Response[] responses) {
            mTask = null;

            for (Response response : responses) {
                if (!response.success) {
                    mSuccess = false;
                }
            }

            if (!mSuccess) {
                Toast.makeText(SubmitAnswerActivity.this, getString(R.string.error_unknown),
                        Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }
}