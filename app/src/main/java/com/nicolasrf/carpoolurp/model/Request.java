package com.nicolasrf.carpoolurp.model;

public class Request {

    private String request_id;
    private String user_id;
    private String date_created;

    public Request() {
    }

    public Request(String user_id, String date_created) {
        this.user_id = user_id;
        this.date_created = date_created;
    }

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
}