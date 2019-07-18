package xyz.cathal.answerbox;

/**
 * Represents a request to submit a new solution.
 *
 * @author Cathal Conroy
 */

class SubmitAnswerRequest extends Request {
    SubmitAnswerRequest(String session, String title, String body, String subject, int year,
                        int level) {
        String reply = Server.submitAnswer(session, title, body, subject, year, level);
        this.response = gson.fromJson(reply, SubmitAnswerResponse.class);
    }
}
