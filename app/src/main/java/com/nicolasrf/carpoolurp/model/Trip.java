package com.nicolasrf.carpoolurp.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

@SuppressLint("ParcelCreator")
public class Trip implements Parcelable {

    private String address;
    private String latLng;
    private Date date;
    private String dateString;
    private String timeString;
    private Integer seats;
    private Integer cost;
    private boolean isActive;
    private String trip_id;
    private String user_id;
    private Date date_created;
    private List<Request> requests;

    public Trip() {
    }

    public Trip(String address, String latLng, Date date, String dateString, String timeString, Integer seats, Integer cost, boolean isActive, String trip_id, String user_id, Date date_created) {
        this.address = address;
        this.latLng = latLng;
        this.date = date;
        this.dateString = dateString;
        this.timeString = timeString;
        this.seats = seats;
        this.cost = cost;
        this.isActive = isActive;
        this.trip_id = trip_id;
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public Trip(String address, String latLng, Date date, String dateString, String timeString, Integer seats, Integer cost, boolean isActive, String trip_id, String user_id, Date date_created, List<Request> requests) {
        this.address = address;
        this.latLng = latLng;
        this.date = date;
        this.dateString = dateString;
        this.timeString = timeString;
        this.seats = seats;
        this.cost = cost;
        this.isActive = isActive;
        this.trip_id = trip_id;
        this.user_id = user_id;
        this.date_created = date_created;
        this.requests = requests;
    }

    protected Trip(Parcel in) {
        address = in.readString();
        latLng = in.readString();
        dateString = in.readString();
        timeString = in.readString();
        if (in.readByte() == 0) {
            seats = null;
        } else {
            seats = in.readInt();
        }
        if (in.readByte() == 0) {
            cost = null;
        } else {
            cost = in.readInt();
        }
        isActive = in.readByte() != 0;
        trip_id = in.readString();
        user_id = in.readString();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
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

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(latLng);
        parcel.writeString(dateString);
        parcel.writeString(timeString);
        if (seats == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(seats);
        }
        if (cost == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(cost);
        }
        parcel.writeByte((byte) (isActive ? 1 : 0));
        parcel.writeString(trip_id);
        parcel.writeString(user_id);
    }
}