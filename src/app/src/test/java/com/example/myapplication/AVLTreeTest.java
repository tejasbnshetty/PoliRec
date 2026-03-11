package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;
import com.example.myapplication.data.model.AVLTree;

public class AVLTreeTest {

    private AVLTree avlTree;

    @Before
    public void setup() {
        avlTree = new AVLTree();

        // User 1: gkd
        HashMap<String, Object> vehicle1 = new HashMap<>();
        vehicle1.put("make", "Toyota");
        vehicle1.put("model", "Corolla");
        vehicle1.put("type", "Sedan");

        HashMap<String, Object> vehicles1 = new HashMap<>();
        vehicles1.put("987654321", vehicle1);

        HashMap<String, Object> userInfo1 = new HashMap<>();
        userInfo1.put("username", "gkd");
        userInfo1.put("vehicles", vehicles1);

        avlTree.insert("LIC1234", userInfo1);

        // User 2: tejasbns
        HashMap<String, Object> vehicle2 = new HashMap<>();
        vehicle2.put("make", "Honda");
        vehicle2.put("model", "Civic");
        vehicle2.put("type", "Hatchback");

        HashMap<String, Object> vehicles2 = new HashMap<>();
        vehicles2.put("123456789", vehicle2);

        HashMap<String, Object> userInfo2 = new HashMap<>();
        userInfo2.put("username", "tejasbns");
        userInfo2.put("vehicles", vehicles2);

        avlTree.insert("LIC5678", userInfo2);
    }

    @Test
    public void testSearch_user1_Success() {
        HashMap<String, Object> result = avlTree.searchRegoByUsername("gkd", "987654321");
        assertNotNull(result);
        assertEquals("Toyota", result.get("make"));
    }

    @Test
    public void testSearch_user2_Success() {
        HashMap<String, Object> result = avlTree.searchRegoByUsername("tejasbns", "123456789");
        assertNotNull(result);
        assertEquals("Honda", result.get("make"));
    }

    @Test
    public void testSearch_WrongUser1() {
        HashMap<String, Object> result = avlTree.searchRegoByUsername("gkd", "123456789");
        assertNull(result);
    }

    @Test
    public void testSearch_WrongUser2() {
        HashMap<String, Object> result = avlTree.searchRegoByUsername("tejasbns", "987654321");
        assertNull(result);
    }

    @Test
    public void testSearch_UnknownUser() {
        HashMap<String, Object> result = avlTree.searchRegoByUsername("testUser", "123456789");
        assertNull(result);
    }

    @Test
    public void testSearch_UnknownRego() {
        HashMap<String, Object> result = avlTree.searchRegoByUsername("gkd", "98765");
        assertNull(result);
    }
    @Test
    public void testAdminSearch_License1_Success() {
        HashMap<String, Object> result = avlTree.search("LIC1234");
        assertNotNull(result);
        assertEquals("gkd", result.get("username"));
    }

    @Test
    public void testAdminSearch_License2_Success() {
        HashMap<String, Object> result = avlTree.search("LIC5678");
        assertNotNull(result);
        assertEquals("tejasbns", result.get("username"));
    }

    @Test
    public void testAdminSearch_License_NotFound() {
        HashMap<String, Object> result = avlTree.search("LIC9999");
        assertNull(result);
    }

    @Test
    public void testAdminSearch_EmptyLicense() {
        HashMap<String, Object> result = avlTree.search("");
        assertNull(result);
    }

    @Test
    public void testAdminSearch_NullLicense() {
        HashMap<String, Object> result = avlTree.search(null);
        assertNull(result);
    }
}