package com.example.myapplication;

public class RequestItem {

    private String requestId, status, reason, name, mobile, email, address, insurance;

    //contructor
    public RequestItem(String requestId, String status, String reason,
                       String name, String mobile, String email, String address, String insurance) {
        this.requestId = requestId;
        this.status = status;
        this.reason = reason;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.insurance = insurance;
    }

    //getter and setters
    public String getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getInsurance() { return insurance; }
}
