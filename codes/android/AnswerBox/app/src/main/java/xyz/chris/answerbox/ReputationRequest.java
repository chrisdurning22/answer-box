package xyz.cathal.answerbox;

/**
 * Represents a request to update an answers reputation.
 *
 * @author Cathal Conroy
 */

class ReputationRequest extends Request {
    ReputationRequest(String session, int value, int solutionId) {
        super();
        String reply = Server.reputation(session, value, solutionId);
        this.response = gson.fromJson(reply, Response.class);
    }
}
