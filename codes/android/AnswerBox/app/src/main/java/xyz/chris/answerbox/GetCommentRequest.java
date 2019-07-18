package xyz.cathal.answerbox;

/**
 * Created by I329999 on 12/03/2017.
 */

class GetCommentRequest extends Request {
    GetCommentRequest(String sesson, int id) {
        super();
        String reply = Server.getComment(sesson, id);
        this.response = gson.fromJson(reply, CommentResponse.class);
    }
}
