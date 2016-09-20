package com.zweigbergk.speedswede.model;

/**
 * Created by kevin on 20/09/2016.
 */
public class Message {
    private String name;
    private String text;
    private String timeStamp;

    public Message(){

    }

    public Message(String name, String text, String timeStamp){
        this.name = name;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setMessage(String message){
        this.text=message;
    }
    public void setTimeStamp(String timeStamp){
        this.timeStamp=timeStamp;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


}
