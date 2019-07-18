package xyz.cathal.answerbox;

/**
 * This class represents an Answer as defined by the remote database.
 *
 * @author Cathal Conroy
 */

class Answer {

    int id;
    int user_id;
    String title;
    String content;
    int reputation;
    int subject_id;
    int year;
    int level;
    File[] files;
    User user;
    Comment[] comments;
    int vote;

    @Override
    public String toString() {
        return this.title;
    }
}
