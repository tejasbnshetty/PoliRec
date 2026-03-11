package com.example.myapplication;



// the helper class understands policies and helps to set, get policy details
public class HelperPolicy {

    String policy_no;
    String policytxt;

    public HelperPolicy(String policy_no, String policytxt) { // parameterized consstructor
        this.policy_no = policy_no;
        this.policytxt = policytxt;
    }

    public HelperPolicy() {// used to create instance of HelperPolicy in other class files
    }

    public String getPolicy_no() {
        return policy_no;
    }// return policy number
    public String getPolicytxt() { return policytxt; }// return policy description

    public void setPolicytxt(String policytxt) {
        this.policytxt = policytxt;
    }// assigning policy description
    public void setPolicy_no(String policy_no){ this.policy_no = policy_no;}// assigning policy number

}
