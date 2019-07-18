package xyz.cathal.answerbox;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * A helper class to assist with validating user input from various contexts.
 *
 * @author Cathal Conroy
 */

class InputValidator {

    /**
     * Validates an email address.
     *
     * @param input The email address to be checked
     * @return The result of the check
     */
    static boolean isEmailValid(String input) {
        return !TextUtils.isEmpty(input) && Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    /**
     * Validates a username. Usernames must begin with a letter, be between 3 and 20 characters, and
     * must contain only letters, numbers and underscores.
     *
     * @param input The username to be checked
     * @return The result of the check
     */
    static boolean isUsernameValid(String input) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]\\w{2,19}$");
        return pattern.matcher(input).find();
    }

    /**
     * Validates a password. Passwords must:
     * - be at least 6 characters long
     * - contain both upper and lower case letters
     *
     * @param input The password to be checked
     * @return The result of the check
     */
    static boolean isPasswordValid(String input) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$");

        return pattern.matcher(input).find();
    }

    /**
     * Validates a solution's title.
     *
     * @param input The title to be checked
     * @return The result of the check
     */
    static boolean isTitleValid(String input) {
        return input.length() >= 3;
    }

    /**
     * Validates a solution's body.
     * @param input
     * @return The result of the check
     */
    static boolean isBodyValid(String input) {
        return input.length() >= 1;
    }
}
