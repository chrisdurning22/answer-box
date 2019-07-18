package xyz.cathal.answerbox;

/**
 * This class represents a Comment as defined by the remote database.
 *
 * @author Cathal Conroy
 */

class Comment {
    int id;
    int user_id;
    int solution_id;
    String content;
    int valid;
    User user;
}
