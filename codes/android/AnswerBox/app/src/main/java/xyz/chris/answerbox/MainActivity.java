package xyz.cathal.answerbox;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Stack;

/**
 * The applications main activity, containing the navigation bar and managing all fragments.
 *
 * @author Christopher Durning
 */

public class MainActivity extends AppCompatActivity implements HomeFragment.BrowseAnswersCommunicator,
        BrowseAnswersFragment.DisplayAnswersCommunicator,
        SavedAnswersFragment.DisplaySavedAnswerCommunicator {

    private static final String TAG_HOME = "HOME";
    private static final String TAG_SAVED = "SAVED";
    private static final String TAG_PROFILE = "PROFILE";

    private String mSubject;
    private int mYear;
    private int mLevel;

    public HashMap<String, Stack<Fragment>> stacks;
    public String currentItem;
    public OnBackPressedListener onBackPressedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // matches tag with stack
        stacks = new HashMap<>();
        stacks.put(TAG_HOME, new Stack<Fragment>());
        stacks.put(TAG_SAVED, new Stack<Fragment>());
        stacks.put(TAG_PROFILE, new Stack<Fragment>());

        // adds the HomeFragment
        currentItem = TAG_HOME;
        replaceFragment(currentItem, new HomeFragment(), true);

        BottomNavigationView bottomNavigation = (BottomNavigationView)
                findViewById(R.id.bottom_navigation_bar);

        // bottom navigation bar listener
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                if (currentItem.equals(TAG_HOME)) {
                                    while (stacks.get(TAG_HOME).size() > 1) {
                                        stacks.get(TAG_HOME).pop();
                                    }
                                }
                                currentItem = TAG_HOME;
                                break;

                            case R.id.action_saved:
                                currentItem = TAG_SAVED;
                                break;

                            case R.id.action_profile:
                                currentItem = TAG_PROFILE;
                                break;
                        }
                        setCurrentState(currentItem);
                        invalidateOptionsMenu();
                        return true;
                    }
                });
    }

    /**
     * Called when the user clicks an item on the action bar. Normally we would need to use a switch
     * to tell which option has been pressed, but as we only have one option here it is unnecessary
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SubmitAnswerActivity.class);
        intent.putExtra("SUBJECT_ID", mSubject);
        intent.putExtra("YEAR_ID", mYear);
        intent.putExtra("LEVEL_ID", mLevel);
        startActivity(intent);
        return true;
    }

    /**
     * @param menu Displays a button that allows the user to add answers
     * @return boolean True if method is called
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);

        MenuItem item = menu.findItem(R.id.action_new_answer);
        item.setVisible(false);

        if (!currentItem.equals(TAG_HOME)) {
            item.setVisible(false);
        } else if (stacks.get(TAG_HOME).peek() instanceof BrowseAnswersFragment) {
            item.setVisible(true);
        } else if (stacks.get(TAG_HOME).peek() instanceof DisplayAnswerFragment) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onBackPressedListener = null;
    }

    /**
     * Receives data from HomeFragment, sends it to BrowseAnswersFragment
     * Adds BrowseAnswersFragment to container
     * @param year the year of the exam paper
     * @param subject the subject chosen by the user
     * @param level the level chosen by the user
     */
    @Override
    public void sendToBrowseAnswers(int year, String subject, int level) {
        this.mYear = year;
        this.mSubject = subject;
        this.mLevel = level;

        // adds BrowseAnswersFragment to container
        BrowseAnswersFragment browseAnswersFragment = new BrowseAnswersFragment();
        replaceFragment(TAG_HOME, browseAnswersFragment, true);

        //sends data to object
        browseAnswersFragment.receiveData(year, subject, level);
    }

    /**
     * Sends Answer object from BrowseAnswersFragment to DisplayAnswerFragment
     * @param answer Answer object chosen by user from the answers list
     */
    @Override
    public void sendObjectToDisplayAnswersFragment(Answer answer) {
        DisplayAnswerFragment displayAnswerFragment = new DisplayAnswerFragment();
        replaceFragment(TAG_HOME, displayAnswerFragment, true);

        displayAnswerFragment.receiveObject(answer);
    }

    /**
     * Sends Answer object from SavedAnswersFragment to DisplaySavedAnswerFragment
     * @param answer Saved Answer object chosen by user from the saved answers list
     */
    @Override
    public void sendObjectToDisplaySavedAnswerFragment(Answer answer) {
        DisplaySavedAnswerFragment displaySavedAnswerFragment = new DisplaySavedAnswerFragment();
        replaceFragment(TAG_SAVED, displaySavedAnswerFragment, true);

        displaySavedAnswerFragment.receiveObjectFromSavedAnswerFragment(answer);
    }

    /**
     * @param onBackPressedListener Gets OnBackPressedListener objects from classes that used back button
     */
    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    /**
     * displays current fragment in container
     * @param tag keeps track of the three stacks and the fragments inside them
     */
    public void setCurrentState(String tag) {
        // if tag is not mapped to any fragments, else use tag to get fragment
        if (stacks.get(tag).size() == 0) {
            if (tag.equals(TAG_SAVED)) {
                replaceFragment(tag, new SavedAnswersFragment(), true);
            } else if (tag.equals(TAG_PROFILE)) {
                replaceFragment(tag, new ProfileFragment(), true);
            }
        } else {
            replaceFragment(tag, stacks.get(tag).lastElement(), false);
        }
    }

    /**
     * Replaces currently displayed fragment with another
     * @param tag Will match new fragment instance with stack
     * @param fragment The fragment to replace the currently displayed fragment
     * @param notExists If an instance of the fragment doesn't already exist, add one to the stack
     */
    public void replaceFragment(final String tag, Fragment fragment, boolean notExists) {
        if (notExists) {
            stacks.get(tag).push(fragment);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    /**
     * Removes fragment from the top of the stack, and replace it with the one before it
     */
    public void popFragment() {
        Fragment fragment = stacks.get(currentItem).elementAt(stacks.get(currentItem).size() - 2);
        stacks.get(currentItem).pop();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    /**
     * Calls onBackPress from currently displayed class when back button is pressed
     */
    public void onBackPressed() {
        invalidateOptionsMenu();
        if (onBackPressedListener != null) {
            onBackPressedListener.onBackPress();
        } else {
            super.onBackPressed();
        }
    }
}