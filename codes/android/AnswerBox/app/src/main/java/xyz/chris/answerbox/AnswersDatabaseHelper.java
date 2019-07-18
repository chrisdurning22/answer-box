package xyz.cathal.answerbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A helper class to manage database creation and version management.
 *
 * @author Christopher Durning
 */

class AnswersDatabaseHelper extends SQLiteOpenHelper {

    AnswersDatabaseHelper(Context context) {
        super(context, AnswersDatabaseContract.DATABASE_FILE, null,
                AnswersDatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AnswersDatabaseContract.SolutionsTable.CREATE_TABLE);
        db.execSQL(AnswersDatabaseContract.UsersTable.CREATE_TABLE);
        db.execSQL(AnswersDatabaseContract.FilesTable.CREATE_TABLE);
        db.execSQL(AnswersDatabaseContract.SolutionsFilesTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
         * This database is only a cache for online data, so its upgrade policy is to simply discard
         * the data and start over
         */
        db.execSQL(AnswersDatabaseContract.SolutionsTable.DELETE_TABLE);
        db.execSQL(AnswersDatabaseContract.UsersTable.DELETE_TABLE);
        db.execSQL(AnswersDatabaseContract.FilesTable.DELETE_TABLE);
        db.execSQL(AnswersDatabaseContract.SolutionsFilesTable.DELETE_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
