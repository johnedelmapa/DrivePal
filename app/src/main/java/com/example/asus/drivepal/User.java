package com.example.asus.drivepal;

public class User {
    public String fullname, email, licenseNo;

    public User() {

    }

    public User(String fullname, String email, String licenseNo) {
        this.fullname = fullname;
        this.email = email;
        this.licenseNo = licenseNo;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }
}


