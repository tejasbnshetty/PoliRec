package com.example.myapplication;

// this class is responsibe for user session happening & also required for State Design Pattern
public class UserSession {
    private UserState currentState;

    public void setState(UserState state) {
        this.currentState = state;
    }

    public boolean canAddPolicy() {
        return currentState != null && currentState.canAddPolicy();
    }

    public String getRoleName() {
        return currentState != null ? currentState.getRoleName() : "Unknown";
    }
}

