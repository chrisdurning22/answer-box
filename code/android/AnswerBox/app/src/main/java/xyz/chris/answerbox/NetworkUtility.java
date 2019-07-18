package xyz.cathal.answerbox;

import java.io.IOException;

/**
 * Checks for an internet connection
 * @author Cathal Conroy
 */

class NetworkUtility {
    /**
     *
     * @return boolean True if there is an active internet connection
     * @throws InterruptedException
     * @throws IOException
     */
    static boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
}
