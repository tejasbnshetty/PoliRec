package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static org.hamcrest.Matchers.not;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserAppealsActivityTest {

    private static final String TEST_USERNAME = "testUser123";
    private static final String TEST_REQUEST_ID = "reqABC123";

    @Before
    public void launchActivity() {
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                UserAppealsActivity.class
        );
        intent.putExtra("username", TEST_USERNAME);
        intent.putExtra("requestId", TEST_REQUEST_ID);
        ActivityScenario.launch(intent);
    }

    @Test
    public void testPrefilledFields_AreCorrectAndDisabled() {
        onView(withId(R.id.appealedUser))
                .check(matches(withText(TEST_USERNAME)))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.appealReqID))
                .check(matches(withText(TEST_REQUEST_ID)))
                .check(matches(not(isEnabled())));
    }

    @Test
    public void testSubmitButton_IsDisabledInitially() {
        onView(withId(R.id.submitAppealButton))
                .check(matches(not(isEnabled())));
    }

    @Test
    public void testTypingReason_EnablesButtonAndChangesColor() {
        onView(withId(R.id.appealDetailsInput))
                .perform(typeText("This is a valid reason."), closeSoftKeyboard());

        onView(withId(R.id.submitAppealButton))
                .check(matches(isEnabled()));
    }
}