package com.example.myapplication;

import android.widget.EditText;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void validateUsername_Empty() {
        activityRule.getScenario().onActivity(activity -> {
            EditText username = activity.findViewById(R.id.login_username);
            username.setText("");
            assertFalse(activity.validateUsername());
        });
    }

    @Test
    public void validateUsername_NotEmpty() {
        activityRule.getScenario().onActivity(activity -> {
            EditText username = activity.findViewById(R.id.login_username);
            username.setText("testuser");
            assertTrue(activity.validateUsername());
        });
    }

    @Test
    public void validatePassword_Empty() {
        activityRule.getScenario().onActivity(activity -> {
            EditText password = activity.findViewById(R.id.login_password);
            password.setText("");
            assertFalse(activity.validatePassword());
        });
    }

    @Test
    public void validatePassword_NotEmpty() {
        activityRule.getScenario().onActivity(activity -> {
            EditText password = activity.findViewById(R.id.login_password);
            password.setText("password123");
            assertTrue(activity.validatePassword());
        });
    }

    @Test
    public void loginWithInvalidCredentials_ShowsError() {
        onView(withId(R.id.login_username)).perform(typeText("wronguser"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("wrongpass"), closeSoftKeyboard());

        onView(withId(R.id.login_button)).perform(click());
        try {
            Thread.sleep(3000); // 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.login_username)).check(matches(hasErrorText("Invalid User")));
    }
    @Test
    public void loginWithValidCredentials_OpensDashboardUser() {
        Intents.init();
        onView(withId(R.id.login_username)).perform(typeText("gkd"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("gkd"), closeSoftKeyboard());

        onView(withId(R.id.login_button)).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intended(hasComponent(DashboardUser.class.getName()));
        Intents.release();
    }

    // Admin login test case
    @Test
    public void loginWithAdminCredentials_OpensDashboardAdmin() {
        Intents.init();
        onView(withId(R.id.login_username)).perform(typeText("admin"), closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("adminpass"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intended(hasComponent(DashboardAdmin.class.getName()));
        Intents.release();
    }
}
