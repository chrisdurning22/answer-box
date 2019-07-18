package xyz.cathal.answerbox;

/**
 * Represents a request to browse for solutions.
 *
 * @author Cathal Conroy
 */

class BrowseRequest extends Request {
    BrowseRequest(String session, int year, String subject, int level) {
        super();
        String reply = Server.browse(session, year, subject, level);
        this.response = gson.fromJson(reply, BrowseResponse.class);
    }
}
