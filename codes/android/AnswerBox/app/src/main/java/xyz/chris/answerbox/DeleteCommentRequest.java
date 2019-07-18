package xyz.cathal.answerbox;

/**
 * Represents a request to submit a comment.
 *
 * @author Cathal Conroy
 */

class DeleteCommentRequest extends Request {
    /**
     *
     * @param session
     * @param commentId
     */
    DeleteCommentRequest(String session, int commentId) {
        super();
        String reply = Server.deleteComment(session, commentId);
        this.response = gson.fromJson(reply, Response.class);
    }
}
