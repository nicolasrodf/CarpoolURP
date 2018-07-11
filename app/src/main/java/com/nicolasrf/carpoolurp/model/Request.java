package com.nicolasrf.carpoolurp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

//*** Va Parcelable para que se pueda pasar la lista de requests desde un objeto Trip.

public class Request implements Parcelable{

    private String request_id;
    private String user_id;
    private Date date_created;

    public Request() {
    }

    public Request(String user_id, Date date_created, String request_id) {
        this.user_id = user_id;
        this.date_created = date_created;
        this.request_id = request_id;
    }

    protected Request(Parcel in) {
        request_id = in.readString();
        user_id = in.readString();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(request_id);
        parcel.writeString(user_id);
    }
}
