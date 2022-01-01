package com.example.SupervisorP.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Schedule implements Serializable {
    @Exclude
    private String key;
    private String date;
    private String duration;
    private String timeStart;
    private String timeEnd;
    private String interruptTime;
    private String sum;
    public Schedule(){}

    public Schedule(String date, String duration, String timeStart, String timeEnd, String interruptTime, String sum) {
        this.date = date;
        this.duration = duration;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.interruptTime = interruptTime;
        this.sum = sum;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getInterruptTime() {
        return interruptTime;
    }

    public String getSum() {
        return sum;
    }
}
