package com.example.myapplication;

import static org.junit.Assert.*;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.lang.reflect.Method;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class TokeniserTest {

    private Map<String, String> invokeTokenizeInput(VehicleSearch activity, String input) throws Exception {
        Method method = VehicleSearch.class.getDeclaredMethod("tokenizeInput", String.class);
        method.setAccessible(true);
        return (Map<String, String>) method.invoke(activity, input);
    }

    @Test
    public void testSingleFilter() {
        ActivityScenario<VehicleSearch> scenario = ActivityScenario.launch(VehicleSearch.class);
        scenario.onActivity(activity -> {
            try {
                Map<String, String> filters = invokeTokenizeInput(activity, "make:toyota");
                assertEquals("toyota", filters.get("make"));
            } catch (Exception e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }

    @Test
    public void testMultiFilters() {
        ActivityScenario<VehicleSearch> scenario = ActivityScenario.launch(VehicleSearch.class);
        scenario.onActivity(activity -> {
            try {
                Map<String, String> filters = invokeTokenizeInput(activity, "make:tesla type:suv");
                assertEquals(2, filters.size());
                assertEquals("tesla", filters.get("make"));
                assertEquals("suv", filters.get("type"));
            } catch (Exception e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }

    @Test
    public void testQuotedValue() {
        ActivityScenario<VehicleSearch> scenario = ActivityScenario.launch(VehicleSearch.class);
        scenario.onActivity(activity -> {
            try {
                Map<String, String> filters = invokeTokenizeInput(activity, "make:\"land rover\" model:defender");
                assertEquals("land rover", filters.get("make"));
                assertEquals("defender", filters.get("model"));
            } catch (Exception e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }

    @Test
    public void testInvalidFilterKey() {
        ActivityScenario<VehicleSearch> scenario = ActivityScenario.launch(VehicleSearch.class);
        scenario.onActivity(activity -> {
            try {
                Map<String, String> filters = invokeTokenizeInput(activity, "color:red");
                assertNull(filters);
            } catch (Exception e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }

    @Test
    public void testMixedValidAndInvalid() {
        ActivityScenario<VehicleSearch> scenario = ActivityScenario.launch(VehicleSearch.class);
        scenario.onActivity(activity -> {
            try {
                Map<String, String> filters = invokeTokenizeInput(activity, "make:bmw color:red");
                assertEquals(1, filters.size());
                assertTrue(filters.containsKey("make"));
            } catch (Exception e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }

    @Test
    public void testEmptyInput() {
        ActivityScenario<VehicleSearch> scenario = ActivityScenario.launch(VehicleSearch.class);
        scenario.onActivity(activity -> {
            try {
                Map<String, String> filters = invokeTokenizeInput(activity, "");
                assertNull(filters);
            } catch (Exception e) {
                fail("Exception: " + e.getMessage());
            }
        });
    }
}