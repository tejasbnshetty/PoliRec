package com.example.myapplication;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class AdminRequestsActivityTest {

    @Test
    public void testBackButtonIsDisplayedAndClickable() {
        ActivityScenario.launch(AdminRequestsActivity.class);
        Espresso.onView(withId(R.id.back_button))
                .check(ViewAssertions.matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void testRequestContainerIsVisible() {
        ActivityScenario.launch(AdminRequestsActivity.class);
        Espresso.onView(withId(R.id.request_container))
                .check(ViewAssertions.matches(isDisplayed()));
    }
}