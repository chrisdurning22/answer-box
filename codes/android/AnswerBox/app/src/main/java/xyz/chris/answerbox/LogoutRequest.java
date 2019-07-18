package xyz.cathal.answerbox;

/**
 * Represents a request to log a user out.
 *
 * @author Cathal Conroy
 */

class LogoutRequest extends Request {

    LogoutRequest(String session) {
        super();
        String reply = Server.logout(session);
        this.response = gson.fromJson(reply, Response.class);
    }
}
