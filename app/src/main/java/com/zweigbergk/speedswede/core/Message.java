package com.zweigbergk.speedswede.core;

import java.util.Date;
public class Message {
    private String name;
    private String text;
    private final long timeStamp;

    //For JSON de-serialization
    private Message() {
        timeStamp = 0;
    }

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

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public void copyTextFrom(Message message) {
        this.text = message.getText();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }

        Message otherMessage = (Message) other;

        // todo: Change this to use getId instead /Andreas
        return otherMessage.getName().equals(this.getName())
                && otherMessage.getTimeStamp() == this.getTimeStamp();
    }

}
