package com.example.asus.drivepal.models;

public class Violation {

    String violationtype, fullname, licenseno, plateno, color, manufacturer, model;

    public Violation() {

    }

    public Violation(String violationtype, String fullname, String licenseno, String plateno, String color, String manufacturer, String model) {
        this.violationtype = violationtype;
        this.fullname = fullname;
        this.licenseno = licenseno;
        this.plateno = plateno;
        this.color = color;
        this.manufacturer = manufacturer;
        this.model = model;
    }

    public String getViolationtype() {
        return violationtype;
    }

    public void setViolationtype(String violationtype) {
        this.violationtype = violationtype;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLicenseno() {
        return licenseno;
    }

    public void setLicenseno(String licenseno) {
        this.licenseno = licenseno;
    }

    public String getPlateno() {
        return plateno;
    }

    public void setPlateno(String plateno) {
        this.plateno = plateno;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
