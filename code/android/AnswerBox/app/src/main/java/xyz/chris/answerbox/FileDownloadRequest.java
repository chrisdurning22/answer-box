package xyz.cathal.answerbox;

/**
 * Represents a request to download a file.
 *
 * @author Cathal Conroy
 */

class FileDownloadRequest extends Request {
    FileDownloadRequest(String session, int id) {
        super();
        String reply = Server.downloadFile(session, id);
        this.response = gson.fromJson(reply, FileDownloadResponse.class);
    }
}
