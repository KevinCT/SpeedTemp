package com.zweigbergk.speedswede.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.LocalStorage;
import com.zweigbergk.speedswede.util.AbuseFilter;
import com.zweigbergk.speedswede.util.ChildCountListener;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.collection.MapExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.Time;

import static com.zweigbergk.speedswede.Constants.DEAFULT_TOPIC_IMAGE;
import static com.zweigbergk.speedswede.Constants.MAX_PREVIEW_LENGTH;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private enum Event { CHAT_VIEW_CLICKED, CHAT_REMOVED, CHAT_ADDED }

    private static final String TAG = ChatAdapter.class.getSimpleName().toUpperCase();

    private ListExtension<Chat> mChats;
    private MapExtension<Event, ListExtension<Client<Chat>>> eventClients;
    private Context mContext;
    private ChildCountListener mChildCountListener;

    private ChatAdapter(ListExtension<Chat> chats) {
        eventClients = new HashMapExtension<>();
        mChats = chats;

        for (Event event : Event.values()) {
            eventClients.put(event, new ArrayListExtension<>());
        }

    }



    public void setView(ChildCountListener childCountListener) {
       mChildCountListener = childCountListener;
    }

    public ChatAdapter() {
        this(new ArrayListExtension<>());
    }

    public final void notifyChange(DataChange<Chat> change) {
        Chat chat = change.getItem();
        DatabaseEvent event = change.getEvent();

        Log.d(TAG, "notifyChange.");

        mChildCountListener.onUpdate();

        switch (event) {
            case ADDED:
                Log.d(TAG, "notifyChange: chat added.");
                addChat(chat);
                break;
            case CHANGED:
                Log.d(TAG, "notifyChange: chat changed?");
                User activeUser = DatabaseHandler.getActiveUser();
                if (!chat.includesUser(activeUser)) {
                    removeChat(chat);
                } else {
                    updateChat(chat);
                }
                break;
            case REMOVED:
                removeChat(chat);
                break;
            default:
                break;
        }
    }

    public void addClickEventClient(Client<Chat> client) {
        eventClients.get(Event.CHAT_VIEW_CLICKED).add(client);
    }

    private void updateChat(@NonNull Chat chat) {
        int index = mChats.indexOf(chat);
        if (index != -1) {
            mChats.set(index, chat);
            notifyItemChanged(index);
        }
    }

    private void addChat(Chat chat) {
        Log.d(TAG, "In addChat");

        mChildCountListener.onUpdate();

        if (!mChats.contains(chat)) {
            Log.d(TAG, "Did not contain our chat.");
            mChats.add(chat);
            notifyItemInserted(getItemCount() - 1);
            broadcastEvent(Event.CHAT_ADDED, chat);
        } else {
            Log.d(TAG, "Contained our chat.");
            mChats.set(mChats.indexOf(chat), chat);
            notifyItemChanged(getItemCount() - 1);
        }

        Log.d(TAG, mChats.size() + "");
    }

    private void removeChat(Chat chat) {
        int position = mChats.indexOf(chat);

        if (position != -1) {
            Log.d(TAG, "In removeChat, position: " + position);

            mChats.remove(chat);
            notifyItemRemoved(position);

            broadcastEvent(Event.CHAT_REMOVED, chat);
        }
    }

    public ListExtension<Chat> getChats() {
        return mChats;
    }

    private void broadcastEvent(Event event, Chat chat) {
        ListExtension<Client<Chat>> clients = eventClients.get(event);
        for (Client<Chat> client : clients) {
            client.supply(chat);
        }
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        chat.setName(LocalStorage.INSTANCE.getString(mContext,chat.getId(),chat.getName()));

        Message latestMessage = chat.getLatestMessage();


        //SetExtension appropriate text as latest message text
        String messageText = "";
        if (latestMessage != null) {
            messageText = latestMessage.getText();

            String activeUid = DatabaseHandler.getActiveUserId();
            String filteredMessage = AbuseFilter.filterMessage(messageText);

            if (latestMessage.getId().equals(activeUid)) {
                messageText = "You: " + filteredMessage;
            } else {
                messageText = "Other person: " + filteredMessage;
            }
        }

        if (messageText.length() > MAX_PREVIEW_LENGTH) {
            messageText = messageText.substring(0, MAX_PREVIEW_LENGTH) + "...";
        }

        String formattedTime = latestMessage != null ? Time.formatMessageDate(latestMessage) : "";

        //Default
        Constants.Topic topic = Constants.Topic.fromString(chat.getName());
        int topicResId = topic != null ? topic.getResourceId() : DEAFULT_TOPIC_IMAGE;

        holder.name.setText(chat.getName());
        holder.latestMessage.setText(messageText);
        holder.timestamp.setText(formattedTime);
        holder.topicImage.setImageDrawable(ContextCompat.getDrawable(mContext, + topicResId));

        holder.mView.setOnClickListener(chatView -> broadcastEvent(Event.CHAT_VIEW_CLICKED, chat));
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView latestMessage;
        TextView timestamp;
        ImageView topicImage;
        View mView;

        ViewHolder(View view) {
            super(view);

            mView = view;
            name = (TextView) view.findViewById(R.id.row_chat_list_item_name);
            latestMessage = (TextView) view.findViewById(R.id.row_chat_list_item_latest_message);
            timestamp = (TextView) view.findViewById(R.id.row_chat_list_item_timestamp);
            topicImage = (ImageView) view.findViewById(R.id.topic_image);
        }
    }
}