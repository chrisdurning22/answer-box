package xyz.cathal.answerbox;

/**
 * Represents a response from the server, following a RegisterResponse.
 *
 * @author Cathal Conroy
 */
class RegisterResponse extends Response {

    int id;
    String username;
    String email;
    int reputation;
}
