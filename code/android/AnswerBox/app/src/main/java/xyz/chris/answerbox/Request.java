package xyz.cathal.answerbox;

import com.google.gson.Gson;

/**
 * An abstract superclass representing a generic request made to the server.
 *
 * @author Cathal Conroy
 */

abstract class Request {

    Response response;
    Gson gson;

    Request() {
        this.gson = new Gson();
    }
}
