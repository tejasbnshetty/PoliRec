package com.example.myapplication.data.model;


public class UpdateRequests {

    public String reqID;
    public String username;
    public String name;
    public String mobileNumber;
    public String address;
    public String email;
    public String insuranceProvider;
    public String status;
    public boolean notified;
    public String reason;

    public UpdateRequests() {
    }

    public UpdateRequests(String reqID, String username, String name, String mobileNumber, String address, String email, String insuranceProvider, String status, boolean notified, String reason) {
        this.reqID = reqID;
        this.username = username;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.email = email;
        this.insuranceProvider = insuranceProvider;
        this.status = status;
        this.notified = notified;
        this.reason = reason;
    }
}