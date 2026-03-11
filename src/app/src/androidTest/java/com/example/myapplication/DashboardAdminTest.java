package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DashboardAdminTest {

    @Before
    public void setup() {
        init();
    }

    @After
    public void tearDown() {
        release();
    }

    private void launchDashboard() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.myapplication", "com.example.myapplication.DashboardAdmin");
        intent.putExtra("username", "testadmin");
        ActivityScenario.launch(intent);
    }

    // Profile page test
    @Test
    public void testProfileButtonNavigatesToUserDetails() {
        launchDashboard();
        onView(withId(R.id.user_profile_admin)).perform(click());
        intended(hasComponent(UserDetails.class.getName()));
    }

    // Notifications page test
    @Test
    public void testRequestsButtonNavigatesToAdminRequestsActivity() {
        launchDashboard();
        onView(withId(R.id.notification_admin)).perform(click());
        intended(hasComponent(AdminRequestsActivity.class.getName()));
    }

    // Policies page test
    @Test
    public void testPolicyButtonNavigatesToPoliciesActivity() {
        launchDashboard();
        onView(withId(R.id.policy_admin)).perform(click());
        intended(hasComponent(PoliciesActivity.class.getName()));
    }

    // Advanced search button directs to vehicle search page test
    @Test
    public void testAdvancedSearchButtonNavigatesToVehicleSearch() {
        launchDashboard();
        onView(withId(R.id.search_button_advanced)).perform(click());
        intended(hasComponent(VehicleSearch.class.getName()));
    }
}
