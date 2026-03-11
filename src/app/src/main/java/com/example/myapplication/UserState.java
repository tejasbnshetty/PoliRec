package com.example.myapplication;


// required for userState also a part of State Design pattern
public interface UserState {
    boolean canAddPolicy();
    String getRoleName();
}

