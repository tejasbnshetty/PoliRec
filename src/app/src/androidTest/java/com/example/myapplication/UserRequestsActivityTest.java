package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserRequestsActivityTest {

    private UserRequestsActivity activity;

    @Rule
    public ActivityScenarioRule<UserRequestsActivity> activityRule =
            new ActivityScenarioRule<>(intentWithUsername());

    private static Intent intentWithUsername() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserRequestsActivity.class);
        intent.putExtra("username", "testUser");
        return intent;
    }

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(a -> activity = a);
    }

    @Test
    public void testGenerateRequestIdFormat() throws Exception {
        String id = activity.generateRequestId();
        assertNotNull(id);
        assertTrue(id.startsWith("req"));
        assertEquals(7, id.length());
    }

    @Test
    public void testSubmitButtonDisabledWhenNoInput() {
        onView(withId(R.id.buttonSubmitRequest))
                .check(matches(not(isEnabled())));
    }

    @Test
    public void testSubmitButtonDisabledWhenOnlyReasonFilled() {
        onView(withId(R.id.editTextReason)).perform(replaceText("Need update"));
        onView(withId(R.id.buttonSubmitRequest))
                .check(matches(not(isEnabled())));
    }

    @Test
    public void testSubmitButtonDisabledWhenOnlyOptionalFieldFilled() {
        onView(withId(R.id.editTextName)).perform(replaceText("John"));
        onView(withId(R.id.buttonSubmitRequest))
                .check(matches(not(isEnabled())));
    }
    @Test
    public void testSubmitButtonEnabledWhenReasonAndOneOtherFieldFilled() {
        onView(withId(R.id.editTextReason)).perform(replaceText("Update required"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("john@example.com"));
        onView(withId(R.id.buttonSubmitRequest))
                .check(matches(isEnabled()));
    }
    @Test
    public void testSubmitButtonEnabledWhenReasonAndMultipleFieldsFilled() {
        onView(withId(R.id.editTextReason)).perform(replaceText("Correction needed"));
        onView(withId(R.id.editTextName)).perform(replaceText("Alice"));
        onView(withId(R.id.editTextMobile)).perform(replaceText("1234567890"));
        onView(withId(R.id.buttonSubmitRequest))
                .check(matches(isEnabled()));
    }

}
