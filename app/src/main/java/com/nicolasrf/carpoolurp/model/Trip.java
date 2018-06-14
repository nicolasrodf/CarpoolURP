package com.nicolasrf.carpoolurp.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by Nicolas on 13/06/2018.
 */

public class Trip {

    private String address;
    private LatLng latLng;
    private Date date;
    private String dateString;
    private String timeString;
    private Integer seats;
    private Integer cost;
    private boolean isActive;

    public Trip() {
    }

    public Trip(String address, LatLng latLng, Date date, String dateString, String timeString, Integer seats, Integer cost, boolean isActive) {
        this.address = address;
        this.latLng = latLng;
        this.date = date;
        this.dateString = dateString;
        this.timeString = timeString;
        this.seats = seats;
        this.cost = cost;
        this.isActive = isActive;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
