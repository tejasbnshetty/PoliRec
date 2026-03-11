package com.example.myapplication;



// this class is responsible for having policies in database as in structuring the policies.
public class Policy {
    private String policyNumber;
    private String policyDescription;

    public Policy(String policyNumber, String policyDescription) { // parameterized constructor to use later in other places.
        this.policyNumber = policyNumber;
        this.policyDescription = policyDescription;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }// return policyno

    public String getPolicyDescription() {
        return policyDescription;
    }// return description of policy
}
