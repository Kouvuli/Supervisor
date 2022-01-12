package com.example.SupervisorP.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class History implements Serializable {
    @Exclude
    private String key;
    private String date;
    private String timeStart;
    private String timeEnd;

    public String getKeyLog() {
        return keyLog;
    }

    private String keyLog;
    public History(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public History(String date, String timeStart, String timeEnd) {
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }
}
