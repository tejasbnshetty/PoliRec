package com.example.myapplication.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AVLTree {
    private TreeNode root;

    public TreeNode getRoot() {
        return root;
    }

    // Get height
    private int height(TreeNode N) {
        if (N == null) return 0;
        return N.height;
    }

    // Get balance factor
    private int getBalance(TreeNode N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

    // Right rotate
    private TreeNode rightRotate(TreeNode y) {
        TreeNode x = y.left;
        TreeNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Left rotate
    private TreeNode leftRotate(TreeNode x) {
        TreeNode y = x.right;
        TreeNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    // Insert
    public void insert(String license, HashMap<String, Object> userInfo) {
        root = insertRec(root, license, userInfo);
    }

    //Insert the user record
    private TreeNode insertRec(TreeNode node, String license, HashMap<String, Object> userInfo) {
        if (node == null) return new TreeNode(license, userInfo);

        if (license.compareTo(node.license) < 0)
            node.left = insertRec(node.left, license, userInfo);
        else if (license.compareTo(node.license) > 0)
            node.right = insertRec(node.right, license, userInfo);
        else // duplicate keys not allowed
            return node;

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        // Left Left
        if (balance > 1 && license.compareTo(node.left.license) < 0)
            return rightRotate(node);

        // Right Right
        if (balance < -1 && license.compareTo(node.right.license) > 0)
            return leftRotate(node);

        // Left Right
        if (balance > 1 && license.compareTo(node.left.license) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left
        if (balance < -1 && license.compareTo(node.right.license) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    // Search
    public HashMap<String, Object> search(String license) {
        if (license == null) return null; // Adding implementation for null, based on test cases.
        return searchRec(root, license);
    }

    //Search using license number of user
    private HashMap<String, Object> searchRec(TreeNode node, String license) {
        if (node == null) return null;
        if (license.equals(node.license)) return node.userInfo;

        if (license.compareTo(node.license) < 0)
            return searchRec(node.left, license);
        else
            return searchRec(node.right, license);
    }

    // New helper method to search by username and rego number
    public HashMap<String, Object> searchRegoByUsername(String username, String rego) {
        return searchRegoHelper(root, username, rego);
    }

    //method for rego search
    private HashMap<String, Object> searchRegoHelper(TreeNode node, String username, String rego) {
        if (node == null) return null;

        if (node.userInfo != null && username.equals(node.userInfo.get("username"))) {
            HashMap<String, Object> vehicles = (HashMap<String, Object>) node.userInfo.get("vehicles");
            if (vehicles != null && vehicles.containsKey(rego)) {
                return (HashMap<String, Object>) vehicles.get(rego);
            }
        }

        HashMap<String, Object> leftSearch = searchRegoHelper(node.left, username, rego);
        if (leftSearch != null) return leftSearch;

        return searchRegoHelper(node.right, username, rego);
    }

    //Helper method for vehicle search by using filters
    public List<HashMap<String, Object>> searchByFilters(Map<String, String> filters) {
        List<HashMap<String, Object>> results = new ArrayList<>();

        // Start the search across the entire tree
        searchByFiltersRecursive(root, filters, results);
        return results;
    }

    //Mehtod to search suing defined filters
    private void searchByFiltersRecursive(TreeNode node, Map<String, String> filters, List<HashMap<String, Object>> result) {
        if (node == null) return;

        // Check the current node's userInfo for matching vehicles
        if (node.userInfo != null) {
            Map<String, HashMap<String, Object>> vehicles = (Map<String, HashMap<String, Object>>) node.userInfo.get("vehicles");

            if (vehicles != null) {
                for (Object vehicleObj : vehicles.values()) {
                    HashMap<String, Object> vehicle = (HashMap<String, Object>) vehicleObj;
                    boolean match = true;

                    // Applying each filter (make, model, type)
                    if (filters.containsKey("make") && !filters.get("make").equalsIgnoreCase((String) vehicle.get("make"))) match = false;
                    if (filters.containsKey("model") && !filters.get("model").equalsIgnoreCase((String) vehicle.get("model"))) match = false;
                    if (filters.containsKey("type") && !filters.get("type").equalsIgnoreCase((String) vehicle.get("type"))) match = false;
                    if (filters.containsKey("fuel") && !filters.get("fuel").equalsIgnoreCase((String) vehicle.get("fuel"))) match = false;
                    if (filters.containsKey("transmission") && !filters.get("transmission").equalsIgnoreCase((String) vehicle.get("transmission"))) match = false;

                    // If all filters match, add the vehicle to the results
                    if (match) result.add(vehicle);
                }
            }
        }

        // Continue searching on both the left and right subtrees
        searchByFiltersRecursive(node.left, filters, result);
        searchByFiltersRecursive(node.right, filters, result);
    }

    //method to clear the tree
    public void clear() {
        root = null;
    }
}
