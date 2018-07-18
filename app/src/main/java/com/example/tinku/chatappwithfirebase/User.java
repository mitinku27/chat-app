package com.example.tinku.chatappwithfirebase;

/**
 * Created by TINKU on 2/5/2018.
 */

public class User {
    String name;
    String dp;
    String status;
    String thumnill;
    String userid;
    public User() {
    }

    public User(String name, String image, String status,String thumnill,String userid) {
        this.name = name;
        this.dp = image;
        this.status = status;
        this.thumnill= thumnill;
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return dp;
    }

    public void setImage(String image) {
        this.dp = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumnill() {
        return thumnill;
    }

    public void setThumnill(String thumnill) {
        this.thumnill = thumnill;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
