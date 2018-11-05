package com.example.asus.drivepal.models;

public class Vehicle {

    public String manufacturer, model, type, color, plateno, engineno;

    public Vehicle(){

    }

    public Vehicle(String manufacturer, String model, String type, String color, String plateno, String engineno) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.type = type;
        this.color = color;
        this.plateno = plateno;
        this.engineno = engineno;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlateno() {
        return plateno;
    }

    public void setPlateno(String plateno) {
        this.plateno = plateno;
    }

    public String getEngineno() {
        return engineno;
    }

    public void setEngineno(String engineno) {
        this.engineno = engineno;
    }
}
