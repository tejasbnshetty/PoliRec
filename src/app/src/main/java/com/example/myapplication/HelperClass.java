package com.example.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;



@IgnoreExtraProperties
public class HelperClass {

    String name;
    String email;
    String phno;
    String license;
    String address;
    String username;
    String password;
    String licenseType;

    //constructors
    public HelperClass() {
    }

    public HelperClass(String name, String email, String phno, String license, String address, String username, String password, String licenseType) {
        this.name = name;
        this.email = email;
        this.phno = phno;
        this.license = license;
        this.address = address;
        this.username = username;
        this.password = password;
        this.licenseType = licenseType;
    }

    //getters and setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhno() { return phno; }
    public void setPhno(String phno) { this.phno = phno; }

    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @PropertyName("License_type")
    public String getLicenseType() { return licenseType; }

    @PropertyName("License_type")
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }
}
