package com.example.myapplication;

import com.example.myapplication.data.model.AVLTreeManager;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AdvancedSearchTest {

    private AVLTreeManager avlTreeManager;

    @Before
    public void setUp() {
        avlTreeManager = AVLTreeManager.getInstance();
        HashMap<String, Object> vehicle1 = new HashMap<>();
        vehicle1.put("make", "tesla");
        vehicle1.put("model", "model s");
        vehicle1.put("type", "sedan");

        HashMap<String, Object> vehicle2 = new HashMap<>();
        vehicle2.put("make", "land rover");
        vehicle2.put("model", "defender");
        vehicle2.put("type", "suv");

        HashMap<String, Object> user = new HashMap<>();
        user.put("username", "admin");
        HashMap<String, Object> vehicles = new HashMap<>();
        vehicles.put("V1", vehicle1);
        vehicles.put("V2", vehicle2);
        user.put("vehicles", vehicles);

        avlTreeManager.insert("LIC1000", user);
    }

    @Test
    public void testFilterByMake() {
        Map<String, String> filters = new HashMap<>();
        filters.put("make", "tesla");
        List<HashMap<String, Object>> results = avlTreeManager.searchByFilters(null, filters);
        assertEquals(1, results.size());
        assertEquals("tesla", results.get(0).get("make"));
    }

    @Test
    public void testFilterByTypeSUV() {
        Map<String, String> filters = new HashMap<>();
        filters.put("type", "suv");
        List<HashMap<String, Object>> results = avlTreeManager.searchByFilters(null, filters);
        assertEquals(1, results.size());
        assertEquals("land rover", results.get(0).get("make"));
    }

    @Test
    public void testFilterByMakeAndType() {
        Map<String, String> filters = new HashMap<>();
        filters.put("make", "land rover");
        filters.put("type", "suv");
        List<HashMap<String, Object>> results = avlTreeManager.searchByFilters(null, filters);
        assertEquals(1, results.size());
    }

    @Test
    public void testFilterNoMatch() {
        Map<String, String> filters = new HashMap<>();
        filters.put("make", "audi");
        List<HashMap<String, Object>> results = avlTreeManager.searchByFilters(null, filters);
        assertTrue(results.isEmpty());
    }
}