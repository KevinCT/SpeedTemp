package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.ParcelHelper;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class Chat implements Parcelable {

    private User firstUser;

    private User secondUser;

    private List<Message> messages;
    private String id;
    private long timeStamp;
    private long lastMessageTimeStamp;

    private String name;
    private boolean inactive;

    public Chat() {
    }

    public Chat(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        this.name = Lists.randomPick(Constants.CHAT_TOPICS);

        this.messages = new ArrayList<>();
        timeStamp = (new Date()).getTime();

        id = firstUser.getUid() + "-" + secondUser.getUid();

        inactive = false;
    }

    public Chat(String id, String name, long timeStamp, List<Message> messages, User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        this.id = id;
        this.name = name;
        this.timeStamp = timeStamp;
        this.messages = messages;

        inactive = false;
    }

    public boolean includesUser(User user) {
        return firstUser.equals(user) || secondUser.equals(user);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public boolean isInactive() {
        return inactive;
    }

    @Exclude
    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public long getTimeStamp() {
        return timeStamp;
    }


    public List<Message> getMessages() {
        List<Message> messageListClone = new ArrayList<>();
        Lists.forEach(messages, m -> messageListClone.add(m.clone()));

        return messageListClone;
    }

    public String getId() {
        return id;
    }

    public void setFirstUser(User user) {
        this.firstUser = user;
    }

    public void setSecondUser(User user) {
        this.secondUser = user;
    }

    @Exclude
    public long getIdAsLong() {
        return firstUser.hashCode() * 5 +
                secondUser.hashCode() * 7 +
                timeStamp;
    }

    public Message getLatestMessage() {
        return messages.size() > 0 ? messages.get(messages.size() - 1) : null;
    }

    public void postMessage(User user, Message message) throws IllegalArgumentException {
        if (!includesUser(user)) {
            throw new IllegalArgumentException(String.format("User provided [%s] is invert a member of this chat.", user.getUid()));
        }
        lastMessageTimeStamp = (new Date()).getTime();
        messages.add(message);
    }

    public String getReadableTime() {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(timeStamp));
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!other.getClass().equals(this.getClass())) {
            return false;
        }
        Chat otherChat = (Chat) other;

        return otherChat.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return this.firstUser.hashCode() * 3 +
                this.secondUser.hashCode() * 5 +
                this.id.hashCode() * 7;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "Chat {id: %s,%sname: %s,%sfirstUser: %s,%secondUser: %s,%smessageCount: %d,\n},",
                id, NEWLINE,
                name, NEWLINE,
                firstUser != null ? firstUser.toString() : "null", NEWLINE,
                secondUser != null ? secondUser.toString() : "null", NEWLINE,
                messages.size()
                );
    }

    private static final String NEWLINE = "\n\t\t";


    /**
     * PARCELABLE METHODS BELOW; IGNORE
     */

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(firstUser, 0);
        dest.writeParcelable(secondUser, 0);
        ParcelHelper.writeParcelableList(dest, 0, messages);
        dest.writeString(id);
        dest.writeLong(timeStamp);
        dest.writeLong(lastMessageTimeStamp);
        dest.writeString(name);
        dest.writeInt(inactive ? 1 : 0);
    }

    public Chat(Parcel in) {
        firstUser = in.readParcelable(UserProfile.class.getClassLoader());
        secondUser = in.readParcelable(UserProfile.class.getClassLoader());
        messages = ParcelHelper.readParcelableList(in, Message.class);
        id = in.readString();
        timeStamp = in.readLong();
        lastMessageTimeStamp = in.readLong();
        name = in.readString();
        inactive = in.readInt() != 0;
    }
}
