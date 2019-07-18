package xyz.cathal.answerbox;

/**
 * Represents a request to submit a comment.
 *
 * @author Cathal Conroy
 */

class CommentRequest extends Request {

    CommentRequest(String session, String body, int solutionId) {
        super();
        String reply = Server.submitComment(session, body, solutionId);
        this.response = gson.fromJson(reply, CommentResponse.class);
    }
}