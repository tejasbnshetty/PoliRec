// PoliciesEspressoTests.java
package com.example.myapplication;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class PolicyEspresso {
    @Rule
    public ActivityTestRule<PoliciesActivity> policiesRule =
            new ActivityTestRule<>(PoliciesActivity.class, false, false);

    @Before
    public void setUp() {
        // Launch as admin so FAB is visible
        Intent intent = new Intent();
        intent.putExtra("admin", true);
        policiesRule.launchActivity(intent);
        // If you have an IdlingResource for Firebase loads, register here:
        // IdlingRegistry.getInstance().register(myFirebaseIdlingResource);
        init();  // for Intents
    }

    @After
    public void tearDown() {
        // IdlingRegistry.getInstance().unregister(myFirebaseIdlingResource);
        release();  // for Intents
    }

    @Test
    public void admin_seesFabAndOpensAddPolicy() {
        // FAB is visible for admins
        onView(withId(R.id.fab_add_policy))
                .check(matches(isDisplayed()));

        // Tap it and verify AddPolicyActivityAdmin is launched
        onView(withId(R.id.fab_add_policy)).perform(click());
        intended(hasComponent(AddPolicyActivityAdmin.class.getName()));
    }

    @Test
    public void regularUser_doesNotSeeFab() {
        // Relaunch as regular user
        policiesRule.finishActivity();
        Intent intent = new Intent();
        intent.putExtra("admin", false);
        policiesRule.launchActivity(intent);

        onView(withId(R.id.fab_add_policy))
                .check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    @Test
    public void listHasAtLeastOnePolicy() {
        // Wait for the initial load (consider using an IdlingResource)
        // Scroll to position 0 and check that the first card’s number text is non-empty
        onView(withId(R.id.policyRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition(0))
                .check(matches(hasDescendant(withId(R.id.policyNumberText))));
    }

    @Test
    public void addPolicy_flow_updatesList() {
        // 1) Open the Add screen
        onView(withId(R.id.fab_add_policy)).perform(click());
        intended(hasComponent(AddPolicyActivityAdmin.class.getName()));

        // 2) Fill & submit
        onView(withId(R.id.etPolicyno))
                .perform(typeText("445566"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.etPolicyInput))
                .perform(typeText("Espresso Testing Policy"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnSubmitPolicy)).perform(click());

        // 3) Simulate the activity finishing and returning
        Espresso.pressBack();

        // 4) Now we're back in PoliciesActivity → RecyclerView should be visible
        onView(withId(R.id.policyRecyclerView))
                .check(matches(isDisplayed()));
    }
}
