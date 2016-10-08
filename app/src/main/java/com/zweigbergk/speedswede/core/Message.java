package com.zweigbergk.speedswede.core;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    @Exclude
    public Date getDateSent() {
        return new Date(timeStamp);
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

    public boolean isFromToday() {
        Date now = new Date();

        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(now);
        calendar2.setTime(new Date(timeStamp));
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public String toString() {
        return String.format("Message { id = [%s], text = [%s], timestamp = [%s] }", id, text, timeStamp + "");
    }

}
