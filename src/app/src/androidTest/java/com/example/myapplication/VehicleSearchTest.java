package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.myapplication.data.model.AVLTreeManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class VehicleSearchTest {

    @Before
    public void setup() {
        AVLTreeManager mockManager = new AVLTreeManager();

        HashMap<String, Object> vehicle1 = new HashMap<>();
        vehicle1.put("make", "Toyota");
        vehicle1.put("model", "Corolla");
        vehicle1.put("type", "Sedan");
        vehicle1.put("rego_num", "987654321");
        String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        vehicle1.put("registered_on", todayDate);
        vehicle1.put("fuel", "Petrol");
        vehicle1.put("transmission", "Automatic");
        vehicle1.put("registration_period", 12);

        HashMap<String, Object> vehicles = new HashMap<>();
        vehicles.put("987654321", vehicle1);

        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", "gkd");
        userInfo.put("vehicles", vehicles);

        mockManager.getAvlTree().insert("LIC1234", userInfo);
        AVLTreeManager.setInstance(mockManager);

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences prefs = context.getSharedPreferences("MY_APP_PREFS", Context.MODE_PRIVATE);
        prefs.edit().putString("USERNAME", "gkd").putBoolean("IS_ADMIN", false).apply();
    }

    @Test
    public void testPerformSearch_ShowsVehicleCard() {
        ActivityScenario.launch(VehicleSearch.class);
        onView(withId(R.id.advanced_search_input)).perform(replaceText("make:Toyota"));
        onView(withId(R.id.advanced_search_button)).perform(click());
        onView(withId(R.id.search_result_count)).check(matches(withText("Results: 1")));
        onView(withId(R.id.vehicle_make)).check(matches(withText("Make: Toyota")));
        onView(withId(R.id.vehicle_model)).check(matches(withText("Model: Corolla")));
        onView(withId(R.id.vehicle_type)).check(matches(withText("Type: Sedan")));
        onView(withId(R.id.registration_status_textview))
                .check(matches(withText(containsString("Registration expires"))));
    }

    @Test
    public void testPerformSearch_NoMatchingVehiclesFound() {
        ActivityScenario.launch(VehicleSearch.class);
        onView(withId(R.id.advanced_search_input)).perform(replaceText("make:Ford"));
        onView(withId(R.id.advanced_search_button)).perform(click());
        onView(withId(R.id.advanced_search_input)).check((view, e) -> {
            EditText editText = (EditText) view;
            CharSequence error = editText.getError();
            assert error != null;
            assert error.toString().contains("No matching vehicles found");
        });
    }

    @Test
    public void testPerformSearch_EmptyInput() {
        ActivityScenario.launch(VehicleSearch.class);
        onView(withId(R.id.advanced_search_button)).perform(click());
        onView(withId(R.id.advanced_search_input)).check((view, e) -> {
            EditText editText = (EditText) view;
            CharSequence error = editText.getError();
            assert error != null;
            assert error.toString().contains("Nothing has been entered");
        });
    }

    @Test
    public void testPerformSearch_MultipleFilters() {
        ActivityScenario.launch(VehicleSearch.class);
        onView(withId(R.id.advanced_search_input)).perform(replaceText("make:Toyota type:Sedan"));
        onView(withId(R.id.advanced_search_button)).perform(click());
        onView(withId(R.id.search_result_count)).check(matches(withText("Results: 1")));
        onView(withId(R.id.vehicle_make)).check(matches(withText("Make: Toyota")));
        onView(withId(R.id.vehicle_type)).check(matches(withText("Type: Sedan")));
    }

    @Test
    public void testPerformSearch_ValidAndInvalidFilters_UnknownFilter() {
        ActivityScenario.launch(VehicleSearch.class);
        onView(withId(R.id.advanced_search_input)).perform(replaceText("make:Toyota color:Blue"));
        onView(withId(R.id.advanced_search_button)).perform(click());
        onView(withId(R.id.advanced_search_input)).check((view, e) -> {
            EditText editText = (EditText) view;
            CharSequence error = editText.getError();
            assert error != null;
            assert error.toString().contains("Unknown filter: color");
        });
    }

    @Test
    public void testPerformSearch_InvalidOnly_ShowsUseValidFilterMessage() {
        ActivityScenario.launch(VehicleSearch.class);
        onView(withId(R.id.advanced_search_input)).perform(replaceText("color:Blue"));
        onView(withId(R.id.advanced_search_button)).perform(click());
        onView(withId(R.id.advanced_search_input)).check((view, e) -> {
            EditText editText = (EditText) view;
            CharSequence error = editText.getError();
            assert error != null;
            assert error.toString().contains("Please use valid filters");
        });
    }
}
