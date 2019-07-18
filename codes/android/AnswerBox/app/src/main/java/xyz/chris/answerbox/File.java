package xyz.cathal.answerbox;

/**
 * This class represents a File as defined by the remote database. Not to be confused with
 * java.io.File.
 *
 * @author Cathal Conroy
 */

class File {
    int id;
    int user_id;
    String hash;
    String extension;
    String data;

    String getFileName() {
        return hash + "." + extension;
    }

}
