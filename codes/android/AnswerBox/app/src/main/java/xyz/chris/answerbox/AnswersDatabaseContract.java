package xyz.cathal.answerbox;

/**
 * This contract class is a container for constants that define names for URIs, tables and columns.
 *
 * @author Christopher Durning
 */

class AnswersDatabaseContract {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_FILE = "AnswersDatabase.db";

    // Enforce singleton design pattern
    private AnswersDatabaseContract() {}

    /* Inner class that defines the table contents */
    static class SolutionsTable {
        static final String TABLE_NAME = "solutions";
        static final String COLUMN_NAME_ID = "id";
        static final String COLUMN_NAME_USER_ID = "user_id";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_CONTENT = "content";
        static final String COLUMN_NAME_REPUTATION = "reputation";
        static final String COLUMN_NAME_SUBJECT_ID = "subject_id";
        static final String COLUMN_NAME_YEAR = "year";
        static final String COLUMN_NAME_LEVEL = "level";

        static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + COLUMN_NAME_ID + " INT(11) UNIQUE,"
                + COLUMN_NAME_USER_ID + " INT(11),"
                + COLUMN_NAME_TITLE + " VARCHAR(40),"
                + COLUMN_NAME_CONTENT + " VARCHAR(10000) DEFAULT NULL,"
                + COLUMN_NAME_REPUTATION + " INT(11),"
                + COLUMN_NAME_SUBJECT_ID + " INT(11),"
                + COLUMN_NAME_YEAR + " SMALLINT(4),"
                + COLUMN_NAME_LEVEL + " TINYINT(1))";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static class UsersTable {
        static final String TABLE_NAME = "users";
        static final String COLUMN_NAME_ID = "id";
        static final String COLUMN_NAME_USERNAME = "username";
        static final String COLUMN_NAME_REPUTATION = "reputation";

        static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + COLUMN_NAME_ID + " INT(11) UNIQUE,"
                + COLUMN_NAME_USERNAME + " VARCHAR(20),"
                + COLUMN_NAME_REPUTATION + " INT(11))";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static class FilesTable {
        static final String TABLE_NAME = "files";
        static final String COLUMN_NAME_ID = "id";
        static final String COLUMN_NAME_HASH = "hash";
        static final String COLUMN_NAME_EXTENSION = "extension";

        static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + COLUMN_NAME_ID + " INT(11) UNIQUE,"
                + COLUMN_NAME_HASH + " VARCHAR(32),"
                + COLUMN_NAME_EXTENSION + " VARCHAR(5))";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static class SolutionsFilesTable {
        static final String TABLE_NAME = "solutions_files";
        static final String COLUMN_NAME_SOLUTION_ID = "solution_id";
        static final String COLUMN_NAME_FILE_ID = "file_id";

        static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " ("
                + COLUMN_NAME_SOLUTION_ID + " INT(11),"
                + COLUMN_NAME_FILE_ID + " INT(11))";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
