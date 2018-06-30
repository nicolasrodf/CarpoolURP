package com.nicolasrf.carpoolurp.model;

import android.os.Parcel;
import android.os.Parcelable;

//*** Va Parcelable para que se pueda pasar la lista de requests desde un objeto Trip.

public class Request implements Parcelable{

    private String request_id;
    private String user_id;
    private String date_created;

    public Request() {
    }

    public Request(String user_id, String date_created) {
        this.user_id = user_id;
        this.date_created = date_created;
    }

    protected Request(Parcel in) {
        request_id = in.readString();
        user_id = in.readString();
        date_created = in.readString();
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(request_id);
        dest.writeString(user_id);
        dest.writeString(date_created);
    }
}
