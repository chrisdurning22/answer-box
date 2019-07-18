package xyz.cathal.answerbox;

/**
 * Represents a request to change the users password.
 *
 * @author Cathal Conroy
 */

class ChangePasswordRequest extends Request {
    ChangePasswordRequest(String session, String oldPass, String newPass) {
        super();
        String reply = Server.changePassword(session, oldPass, newPass);
        this.response = gson.fromJson(reply, Response.class);
    }
}
