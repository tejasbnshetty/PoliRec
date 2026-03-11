package com.example.myapplication;




// regular user.... not admin.... returns false to not be able to add policy
public class RegularUserState implements UserState {
    @Override
    public boolean canAddPolicy() {
        return false;
    }// returning false for having permission to add policy or not depending on user or admin

    @Override
    public String getRoleName() {
        return "User";
    }// this returns the role 'user'.
}
