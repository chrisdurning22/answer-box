package xyz.cathal.answerbox;

/**
 * Represents a request to create an account.
 *
 * @author Cathal Conroy
 */

class RegisterRequest extends Request {

    RegisterRequest(String username, String email, String password) {
        String reply = Server.register(username, email, password);
        this.response = gson.fromJson(reply, RegisterResponse.class);
    }
}
