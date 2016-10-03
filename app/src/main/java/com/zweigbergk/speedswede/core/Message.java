package com.zweigbergk.speedswede.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {

    private String id;
    private String text;
    private final long timeStamp;

    //For JSON de-serialization
    private Message() {
        timeStamp = 0;
    }

    public Message(String id, String text, long timeStamp) {
        this.id = id;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    public Message(String id, String text) {
        this(id, text, (new Date()).getTime());
    }

    public void setId(String id){
        this.id = id;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getId() {
        return id;
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

        return otherMessage.getId().equals(this.getId())
                && otherMessage.getTimeStamp() == this.getTimeStamp();
    }

    public Message clone() {
        return new Message(id, text, timeStamp);
    }

    @Override
    public String toString() {
        return String.format("Message { id = [%s], text = [%s], timestamp = [%s] }", id, text, timeStamp + "");
    }

}
