package com.zweigbergk.speedswede.model;

import java.util.Date;

public class Message {
    private String name;
    private String text;
    private long timeStamp;

    public Message(String name, String text, long timeStamp) {
        this.name = name;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    public Message(String name, String text) {
        this(name, text, (new Date()).getTime());
    }

    public void setName(String name){
        this.name = name;
    }

    public void setMessage(String message){
        this.text = message;
    }

    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getTimeStamp() {
        return String.valueOf(timeStamp);
    }


}
