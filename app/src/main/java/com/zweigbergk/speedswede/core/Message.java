package com.zweigbergk.speedswede.core;

import java.util.Date;
public class Message {

    private String uid;
    private String text;
    private final long timeStamp;

    //For JSON de-serialization
    private Message() {
        timeStamp = 0;
    }

    public Message(String uid, String text, long timeStamp) {
        this.uid = uid;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    public Message(String uid, String text) {
        this(uid, text, (new Date()).getTime());
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getUid() {
        return uid;
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
        return otherMessage.getUid().equals(this.getUid())
                && otherMessage.getTimeStamp() == this.getTimeStamp();
    }

    public Message clone() {
        return new Message(uid, text, timeStamp);
    }

}
