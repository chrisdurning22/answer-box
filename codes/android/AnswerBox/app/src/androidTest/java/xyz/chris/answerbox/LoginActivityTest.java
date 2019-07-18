package xyz.cathal.answerbox;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * @author Cathal Conroy
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<LoginActivity> loginActivityTestRule = new IntentsTestRule<>(LoginActivity.class);

    @Test
    public void email_notExist_returns_false() {
        onView(withId(R.id.identifier)).perform(typeText("not@exist.com"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("555555"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button));

        onView(withId(R.id.identifier)).check(matches(hasErrorText("User does not exist")));
    }

    @Test
    public void username_short_returns_false() {
        onView(withId(R.id.identifier)).perform(typeText("kk"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("555555"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button));

        onView(withId(R.id.identifier)).check(matches(hasErrorText("Username is invalid")));
    }

    @Test
    public void username_notExist_returns_false() {
        onView(withId(R.id.identifier)).perform(typeText("kk"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("555555"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button));

        onView(withId(R.id.identifier)).check(matches(hasErrorText("Username does not exist")));
    }

    @Test
    public void verifyLogin_Correct_ReturnsTrue() {
        onView(withId(R.id.identifier)).perform(typeText("cathal"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("555555"), closeSoftKeyboard());
        onView(withId(R.id.email_sign_in_button));

        intending(allOf(
                hasComponent(hasShortClassName(".MainActivity")),
                toPackage("xyz.cathal.answerbox")));
    }
}
