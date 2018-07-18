package com.example.tinku.chatappwithfirebase;

/**
 * Created by TINKU on 2/21/2018.
 */

public class Request {
    String type;
    String key;

    public Request() {
    }

    public Request(String type, String key) {
        this.type = type;
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
