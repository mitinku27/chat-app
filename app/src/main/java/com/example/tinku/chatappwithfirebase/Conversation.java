package com.example.tinku.chatappwithfirebase;

/**
 * Created by TINKU on 2/21/2018.
 */

public class Conversation {
    Boolean seen;
    Long timestamp;
    String key;
    public Conversation() {
    }

    public Conversation(Boolean seen, Long timestamp,String key) {
        this.seen = seen;
        this.timestamp = timestamp;
        this.key= key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
