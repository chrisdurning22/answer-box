package xyz.cathal.answerbox;

import android.util.Log;

import com.goebl.david.Request;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;

import java.io.File;

/**
 * This class represents the remote server which hosts our API. It should not be instantiated.
 *
 * @author Cathal Conroy
 */

final class Server {

    private static final String API = "http://api.cathal.xyz/index.php?action=";
    private static final Webb WEBB = Webb.create();

    private static Request mRequest;

    // Enforce singleton pattern
    private Server() {}

    /**
     * Signs in with a session key.
     *
     * @param session The users session key
     * @return a LoginResponse, encoded in JSON
     */
    static String login(String session) {
        String url = API + "login&session=" + session;
        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            Log.e("LoginActivity", "WebbException");
            return null;
        }
    }

    /**
     * Signs in with a username/email and password.
     *
     * @param identifier The users username or email
     * @param password The users password
     * @return a LoginResponse, encoded in JSON
     */
    static String login(String identifier, String password) {
        boolean usingEmail = InputValidator.isEmailValid(identifier);
        String identityType = usingEmail ? "email" : "username";
        String url = API + "login&" + identityType + "=" + identifier + "&password=" + password;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            return null;
        }
    }

    /**
     * Creates a new account.
     *
     * @param username The users username
     * @param email The users email
     * @param password The users password
     * @return a RegisterResponse, encoded in JSON
     */
    static String register(String username, String email, String password) {
        String url = API + "register&username=" + username + "&email=" + email + "&password=" +
                password;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            return null;
        }
    }

    /**
     * Searches for uploaded solutions matching the parametrized criteria.
     *
     * @param session The users session key
     * @param year The exam year
     * @param subject The exam subject
     * @param level The exam level
     * @return a BrowseResponse, encoded in JSON
     */
    static String browse(String session, int year, String subject, int level) {
        String url = API + "browse&session=" + session + "&year=" + year + "&subject=" +
                subject + "&level=" + level;
        url = url.replace(" ", "%20");

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            return null;
        }
    }

    /**
     * Uploads a file.
     *
     * @param session The users session key
     * @param file The file the user wishes to upload
     * @param solutionId The ID of the solution the file belongs to
     * @return an FileUploadResponse, encoded in JSON
     */
    static String uploadFile(String session, File file, int solutionId) {
        String url = API + "uploadFile&session=" + session;
        String extension = FileUtility.getExtension(file);
        String encodedFile = FileUtility.toBase64(file);

        try {
            mRequest = WEBB.post(url);
            mRequest.param("solution", solutionId);
            mRequest.param("extension", extension);
            mRequest.param("file", encodedFile);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            return null;
        }
    }

    /**
     * Logs the user out of the system by deleting its session key.
     *
     * @param session The users session key
     * @return a LogoutResponse, encoded in JSON
     */
    static String logout(String session) {
        String url = API + "logout&session=" + session;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Changes the users password.
     *
     * @param session The users session key
     * @param oldPass The users old password
     * @param newPass The users new password
     * @return a Response, encoded in JSON
     */
    static String changePassword(String session, String oldPass, String newPass) {
        String url = API + "password&session=" + session + "&old=" + oldPass + "&new=" + newPass;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Submits a new solution.
     *
     * @param session The users session key
     * @param title The title of the solution
     * @param body The body of the solution
     * @return a SubmitAnswerResponse, encoded in JSON
     */
    static String submitAnswer(String session, String title, String body, String subject, int year,
                               int level) {
        String url = API + "submitAnswer&session=" + session;

        try {
            mRequest = WEBB.post(url);
            mRequest.param("title", title);
            mRequest.param("body", body);
            mRequest.param("subject", subject);
            mRequest.param("year", year);
            mRequest.param("level", level);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            return null;
        }
    }

    static String downloadFile(String session, int id) {
        String url = API + "downloadFile&session=" + session + "&id=" + id;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates a solutions reputation.
     *
     * @param session The users session key
     * @param value The value of the new reputatation
     * @param solutionId The solution ID
     * @return a Response, encoded in JSON
     */
    static String reputation(String session, int value, int solutionId) {
        String url = API + "reputation&session=" + session + "&reputation=" + value + "&solution="
                + solutionId;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reports a solution.
     *
     * @param session The users session key
     * @param solutionId The solution ID
     * @return a Response, encoded in JSON
     */
    static String report(String session, int solutionId) {
        String url = API + "report&session=" + session + "&solution=" + solutionId;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Submits a new comment.
     *
     * @param session The users session key
     * @param body The body of the comment
     * @return a CommentResponse, encoded in JSON
     */
    static String submitComment(String session, String body, int solutionId) {
        String url = API + "submitComment&session=" + session;

        try {
            mRequest = WEBB.post(url);
            mRequest.param("body", body);
            mRequest.param("solution", solutionId);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes a comment.
     *
     * @param session The users session key
     * @param commentId The comment ID
     * @return a Response, encoded in JSON
     */
    static String deleteComment(String session, int commentId) {
        String url = API + "deleteComment&session=" + session + "&id=" + commentId;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String getComment(String session, int id) {
        String url = API + "getComment&session=" + session + "&id=" + id;

        try {
            mRequest = WEBB.get(url);
            return mRequest.ensureSuccess().asString().getBody();
        } catch (WebbException e) {
            e.printStackTrace();
            return null;
        }
    }
}