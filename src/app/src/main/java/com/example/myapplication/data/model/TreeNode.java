package com.example.myapplication.data.model;

import java.util.HashMap;

public class TreeNode {
    String license;
    HashMap<String, Object> userInfo;
    TreeNode left, right;
    int height;

    public TreeNode(String license, HashMap<String, Object> userInfo) {
        this.license = license;
        this.userInfo = userInfo;
        height = 1;
    }
}
