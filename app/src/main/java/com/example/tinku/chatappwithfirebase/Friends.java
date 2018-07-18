package com.example.tinku.chatappwithfirebase;

/**
 * Created by TINKU on 2/14/2018.
 */

public class Friends {
    String time;
    String key;
    public Friends() {
    }

    public Friends(String date,String key) {
        this.time = date;
        this.key=key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
