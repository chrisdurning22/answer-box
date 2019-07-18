package xyz.cathal.answerbox;

import java.util.ArrayList;

/**
 * This interface allows Fragments to communicate with one another.
 *
 * @author Cathal Conroy
 */

interface TaskListener {
    void onTaskCompleted(ArrayList<Answer> answers);
}
