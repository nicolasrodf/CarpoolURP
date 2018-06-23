package com.nicolasrf.carpoolurp.model;

/**
 * Created by Nicolas on 16/06/2018.
 */

public class Car {

    private String brand;
    private String model;
    private String color;
    private String license;
    private String user_id;

    public Car() {
    }

    public Car(String brand, String model, String color, String license, String user_id) {
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.license = license;
        this.user_id = user_id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
