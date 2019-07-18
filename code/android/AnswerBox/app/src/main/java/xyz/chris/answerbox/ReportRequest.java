package xyz.cathal.answerbox;

/**
 * Represents a request to the server to report a solution.
 */

class ReportRequest extends Request {
    ReportRequest(String session, int solutionId) {
        super();
        String reply = Server.report(session, solutionId);
        this.response = gson.fromJson(reply, Response.class);
    }
}
