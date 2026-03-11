package com.example.myapplication;

public class AdminState implements UserState {
    @Override
    public boolean canAddPolicy() {
        return true;
    }

    @Override
    public String getRoleName() {
        return "Admin";
    }
}
