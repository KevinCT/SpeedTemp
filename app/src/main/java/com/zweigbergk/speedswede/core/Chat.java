package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.ParcelHelper;

import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;

import java.util.Date;
public class Chat implements Parcelable {

    private User firstUser;

    private User secondUser;


    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean likedByFirstUser = false;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private boolean likedBySecondUser = false;

    private ListExtension<Message> messages;
    private String id;
    private long timeStamp;
    private long lastMessageTimeStamp;

    private String name;
    private boolean inactive;


    @SuppressWarnings("unused")
    public Chat() {
    }

    public Chat(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        this.name = Constants.Topic.getRandom().name();

        this.messages = new ArrayListExtension<>();
        this.timeStamp = (new Date()).getTime();

        id = firstUser.getUid() + "-" + secondUser.getUid();

        inactive = false;
    }

    public Chat(String id, String name, long timeStamp, ListExtension<Message> messages) {
        this.firstUser = null;
        this.secondUser = null;

        this.id = id;
        this.name = name;
        this.timeStamp = timeStamp;
        this.messages = messages;

        inactive = false;
    }

    public boolean includesUser(User user) {
        return firstUser.equals(user) || secondUser.equals(user);
    }

    public User getOtherUser(User user) {
        return getFirstUser().equals(user) ? getSecondUser() : getFirstUser();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getFirstUser() {
        return firstUser;
    }

    @SuppressWarnings("WeakerAccess")
    public User getSecondUser() {
        return secondUser;
    }


    public ListExtension<Message> getMessages() {
        ListExtension<Message> messageListClone = new ArrayListExtension<>();
        Lists.forEach(messages, m -> messageListClone.add(m.clone()));

        return messageListClone;
    }

    public String getId() {
        return id;
    }

    public void setLikeStatusFirstUser(Boolean likeStatus) {
        likedByFirstUser = likeStatus;
    }

    public void setLikeStatusSecondUser(Boolean likeStatus) {
        likedBySecondUser = likeStatus;
    }

    public void setFirstUser(User user) {
        this.firstUser = user;
    }

    public void setSecondUser(User user) {
        this.secondUser = user;
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

    public long getTimeStamp() {
        return this.timeStamp;
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
        return Stringify.curlyFormat("Chat: ( id: {id},%n\t\tname: {name},%n\t\t" +
                "firstUser: {user1},%n\t\tsecondUser: {user2},%n\t\tmessageCount: {count},",
                id, name,
                firstUser != null ? firstUser.toString() : "null",
                secondUser != null ? secondUser.toString() : "null",
                messages.size()
                );
    }

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
        ParcelHelper.writeParcelableList(dest, messages);
        dest.writeString(id);
        dest.writeLong(timeStamp);
        dest.writeLong(lastMessageTimeStamp);
        dest.writeString(name);
        dest.writeInt(inactive ? 1 : 0);
    }

    @SuppressWarnings("WeakerAccess")
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
