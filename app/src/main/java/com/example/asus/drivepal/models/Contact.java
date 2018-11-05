package com.example.asus.drivepal.models;

public class Contact {

    String givenname, middlename, familyname, phonenumber, email, relationship;

    public Contact() {

    }

    public Contact(String givenname, String middlename, String familyname, String phonenumber, String email, String relationship) {
        this.givenname = givenname;
        this.middlename = middlename;
        this.familyname = familyname;
        this.phonenumber = phonenumber;
        this.email = email;
        this.relationship = relationship;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
