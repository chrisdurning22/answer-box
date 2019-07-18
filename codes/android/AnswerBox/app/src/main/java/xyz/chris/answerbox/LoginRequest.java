package xyz.cathal.answerbox;

/**
 * Represents a request to authenticate a user.
 *
 * @author Cathal Conroy
 */

class LoginRequest extends Request {

    LoginRequest(String session) {
        super();
        String reply = Server.login(session);
        this.response = gson.fromJson(reply, LoginResponse.class);
    }

    LoginRequest(String identifier, String password) {
        super();
        String reply = Server.login(identifier, password);
        this.response = gson.fromJson(reply, LoginResponse.class);
    }
}
