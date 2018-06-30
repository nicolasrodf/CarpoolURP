package com.nicolasrf.carpoolurp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    protected Trip(Parcel in) {
        address = in.readString();
        latLng = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        dateString = in.readString();
        timeString = in.readString();
        seats = in.readByte() == 0x00 ? null : in.readInt();
        cost = in.readByte() == 0x00 ? null : in.readInt();
        isActive = in.readByte() != 0x00;
        trip_id = in.readString();
        user_id = in.readString();
        long tmpDate_created = in.readLong();
        date_created = tmpDate_created != -1 ? new Date(tmpDate_created) : null;
        if (in.readByte() == 0x01) {
            requests = new ArrayList<Request>();
            in.readList(requests, Request.class.getClassLoader());
        } else {
            requests = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(latLng);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeString(dateString);
        dest.writeString(timeString);
        if (seats == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(seats);
        }
        if (cost == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(cost);
        }
        dest.writeByte((byte) (isActive ? 0x01 : 0x00));
        dest.writeString(trip_id);
        dest.writeString(user_id);
        dest.writeLong(date_created != null ? date_created.getTime() : -1L);
        if (requests == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(requests);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
}