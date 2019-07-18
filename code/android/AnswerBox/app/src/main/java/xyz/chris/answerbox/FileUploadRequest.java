package xyz.cathal.answerbox;

import java.io.File;

/**
 * Represents a request to upload a file.
 *
 * @author Cathal Conroy
 */

class FileUploadRequest extends Request {

    /**
     * @param session The user's session key, used to authenticate API requests
     * @param file The file to be uploaded
     * @param solutionId The ID of the solution the file is accompanying
     */
    FileUploadRequest(String session, File file, int solutionId) {
        super();
        String reply = Server.uploadFile(session, file, solutionId);
        this.response = gson.fromJson(reply, FileUploadResponse.class);
    }
}