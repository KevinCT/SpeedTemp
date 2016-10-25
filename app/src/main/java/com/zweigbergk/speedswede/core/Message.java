package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.zweigbergk.speedswede.util.Translation.TranslationCache;

import java.util.Calendar;
import java.util.Date;

public class Message implements Parcelable, Cloneable {

    private String id;
    private String text;
    private TranslationCache cache;
    private final long timeStamp;
    private boolean isTranslated;

    //For JSON de-serialization
    public Message() {
        timeStamp = 0;
    }

    public Message(String id, String text, long timeStamp) {
        this.id = id;
        this.text = text;
        this.timeStamp = timeStamp;
        this.isTranslated = false;
    }

    public void invertIsTranslated() {
        this.isTranslated = !this.isTranslated;
    }

    public boolean isTranslated() {
        return this.isTranslated;
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

    @Override
    public int hashCode() {
        return this.getId().hashCode() * 3 +
                (int) this.getTimeStamp() * 5;
    }

    @Exclude
    public void setTranslationCache(TranslationCache cache) {
        //Only update cache if the locale has changed
        if (this.cache == null || !this.cache.getLocale().equals(cache.getLocale())) {
            this.cache = cache;
        }
    }

    public TranslationCache getTranslationCache() {
        return cache;
    }

    @Exclude
    public boolean hasCache() {
        return cache != null;
    }

    public Message clone() {
        try {
            super.clone();
        } catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new Message(id, text, timeStamp);
    }

    @Exclude
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
        return String.format("Message { id = %s, text = %s, timestamp = %s }", id, text, timeStamp + "");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeLong(timeStamp);
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public Message(Parcel in) {
        id = in.readString();
        text = in.readString();
        timeStamp = in.readLong();
    }
}
